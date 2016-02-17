package com.lukedeighton.play.mcros

import com.lukedeighton.play.annotation._
import play.api.data.Form

import scala.reflect.macros.{TypecheckException, blackbox}

class FormImpl(val c: blackbox.Context) {
  import c.universe._

  def primaryCtorParams(tpe: Type): List[Symbol] = primaryCtor(tpe).paramLists.head

  def primaryCtor(tpe: Type): MethodSymbol = tpe.decls.collectFirst {
    case m: MethodSymbol if m.isPrimaryConstructor => m
  }.get

  def annotationParamTrees(a: Annotation) = a.tree.children.tail

  /**
   * Used when multiple annotations can be composed of the given type
   */
  def collectAnnotations(aTpe: Type)(implicit annotations: List[Annotation]): List[List[Tree]] = annotations.collect {
    case a if a.tree.tpe <:< aTpe => annotationParamTrees(a)
  }

  /**
   * Used when only one annotation can be composed of the given type.
   * @return None when the annotation was not found or Some with a list of annotation parameter trees when found
   */
  def findAnnotation(aTpe: Type)(implicit annotations: List[Annotation]): Option[List[Tree]] = {
    annotations.find(a => a.tree.tpe <:< aTpe).map(annotationParamTrees)
  }

  def requiredMsgArg()(implicit annotations: List[Annotation]): Option[Tree] = {
    findAnnotation(typeOf[Required]).flatMap(_.headOption).map(arg => q"requiredMsg = $arg")
  }

  def invalidMsgArg()(implicit annotations: List[Annotation]): Option[Tree] = {
    findAnnotation(typeOf[Invalid]).flatMap(_.headOption).map(arg => q"invalidMsg = $arg")
  }

  def constraint(tpe: Type, treeConverter: List[Tree] => Tree)(implicit annotations: List[Annotation]): Option[Tree] = {
    findAnnotation(tpe).map(treeConverter)
  }

  def regexConverter(constraint: Tree): List[Tree] => Tree = args => {
    val argsUpdated = args.updated(0, q"${args.head}.r")
    q"$constraint(..$argsUpdated)"
  }

  def basicConverter(constraint: Tree): List[Tree] => Tree = args => q"$constraint(..$args)"

  def numberConverter(constraint: Tree, desiredTpe: Type): List[Tree] => Tree = { args =>
    try {
      val numberTree = c.typecheck(args.head, pt = desiredTpe)
      q"$constraint(..${numberTree :: args.tail})"
    } catch {
      case TypecheckException(ex, msg) =>
        c.abort(c.enclosingPosition, s"Annotation Type Mismatch with Parameter Type: \n$msg")
    }
  }

  /**
   * Compose the mapping tree with constraints by chaining verifying calls
   */
  def compose(mapping: Tree, constraints: (Type, List[Tree] => Tree)*)(implicit annotations: List[Annotation]): Tree = {
    constraints.toList.flatMap { case (aTpe: Type, treeConverter: (List[Tree] => Tree)) =>
      constraint(aTpe, treeConverter).toList
    }.foldLeft(mapping) {
      (m, t) => q"$m.verifying($t)"
    }
  }

  //TODO class/ object level validation such as authenticate (usr, pwd) or pwd, confirmPwd
  def mapping(tpe: Type)(implicit annotations: List[Annotation]): Tree = {
    val mapping = tpe match {
      case t if t =:= typeOf[String] =>
        stringMapping
      case t if t =:= typeOf[Int] =>
        numberMapping(q"int", t)
      case t if t =:= typeOf[Long] =>
        numberMapping(q"long", t)
      case t if t =:= typeOf[Short] =>
        numberMapping(q"short", t)
      case t if t =:= typeOf[Byte] =>
        numberMapping(q"byte", t)
      case t if t =:= typeOf[Float] =>
        numberMapping(q"float", t)
      case t if t =:= typeOf[Double] =>
        numberMapping(q"double", t)
      case t if t =:= typeOf[Boolean] =>
        ???
      case t if t <:< typeOf[List[_]] =>
        ???
      case t if t <:< typeOf[Seq[_]] =>
        ???
      case t if t <:< typeOf[Array[_]] =>
        ???
      case t if t =:= typeOf[BigDecimal] =>
        ???
      case t if t =:= typeOf[java.util.UUID] =>
        ???
      case t if t =:= typeOf[java.util.Date] =>
        ???
      case t if t =:= typeOf[java.sql.Date] =>
        ???
      case t if t =:= typeOf[org.joda.time.DateTime] =>
        ???
      case t if t =:= typeOf[org.joda.time.LocalDate] =>
        ???
      case t if t <:< typeOf[Option[_]] =>
        optionalMapping(tpe)
      case t =>
        objectMapping(tpe)
    }

    composeConfirm(composeValidateWith(mapping))
  }

  /**
   * Converts ValidateWith annotations into mappings and composes them onto the mapping tree.
   * @param mapping - the mapping being composed
   */
  def composeValidateWith(mapping: Tree)(implicit annotations: List[Annotation]): Tree = {
    val tpe = typeOf[ValidateWith[_]]
    collectAnnotations(tpe).foldLeft(mapping) { (m, args) =>
      //hack to fix an AST issue with anonymous fns? - showCode and parse corrects the problem with AST??
      //TODO could use c.typecheck - also :: with tail rather than updated? will this fix issues?
      val fixedArgs = if (args.nonEmpty) args.updated(0, c.parse(showCode(args.head))) else args
      val validator: Tree = if (args.length == 1) fixedArgs.head else q"Validator(..$fixedArgs)"
      val term = TermName(c.freshName())
      q"""
        {
          val $term = $validator
          $m.verifying($term.errorMsg, x => $term.isValid(x))
        }
      """
    }
  }

  def composeConfirm(mapping: Tree)(implicit annotations: List[Annotation]): Tree = {
    findAnnotation(typeOf[Confirm]).foldLeft(mapping) { (m, args) =>
      q"confirm(..${m :: args})"
    }
  }

  def numberMapping(tree: Tree, tpe: Type)(implicit annotations: List[Annotation]): Tree = {
    compose(of(tree),
      typeOf[Min[_]] -> numberConverter(q"Constraints.min", tpe),
      typeOf[Max[_]] -> numberConverter(q"Constraints.max", tpe))
  }

  def stringMapping(implicit annotations: List[Annotation]): Tree = {
    compose(of(q"string", requiredMsgArg().toList),
      typeOf[MinLength] -> basicConverter(q"Constraints.minLength"),
      typeOf[MaxLength] -> basicConverter(q"Constraints.maxLength"),
      typeOf[Email] -> basicConverter(q"Constraints.emailAddress"),
      typeOf[Pattern] -> regexConverter(q"Constraints.pattern"))
  }

  def optionalMapping(tpe: Type)(implicit annotations: List[Annotation]): Tree = {
    val oTpe = tpe.typeArgs.head
    val innerMapping = mapping(oTpe)
    q"optional($innerMapping)"
  }

  def objectMapping(tpe: Type)(implicit annotations: List[Annotation]): Tree = {
    //TODO check here for annotations on the class as well?
    val mappings: List[Tree] = primaryCtorParams(tpe).map { param =>
      val pName = param.name.decodedName.toString
      val pAnnotations = param.annotations
      val pMapping = mapping(param.typeSignature)(pAnnotations)
      q"$pName -> $pMapping"
    }
    val companion = tpe.typeSymbol.companion

    q"mapping(..$mappings)($companion.apply)($companion.unapply)"
  }

  def of(tree: Tree)(implicit annotations: List[Annotation]): Tree = {
    of(tree, invalidMsgArg().toList ++ requiredMsgArg().toList)
  }

  def of(tree: Tree, args: List[Tree])(implicit annotations: List[Annotation]): Tree = {
    q"of($tree(..$args))"
  }

  def form[T: c.WeakTypeTag]: Tree = {
    val tpe: Type = weakTypeOf[T]
    val mapping: Tree = objectMapping(tpe)(Nil)

    q"""
      import play.api.data.Form
      import play.api.data.Forms._
      import com.lukedeighton.play.validation._
      import com.lukedeighton.play.validation.Binders._
      import com.lukedeighton.play.validation.Mappings._

      Form($mapping)
    """
  }

  def formExpr[T: c.WeakTypeTag]: c.Expr[Form[T]] = {
    c.Expr[Form[T]](form[T])
  }
}
