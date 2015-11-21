package com.lukedeighton.play.validation

import com.lukedeighton.play.validation.Binders._
import play.api.data.Forms._
import play.api.data.{FormError, Mapping}
import play.api.data.validation.{ValidationError, Invalid, Valid, Constraint}

object Mappings {

  /**
   * Duplicate this mapping into a tuple mapping where the second (duplicated version of the mapping) must equal the first mapping.
   * The Key for this mapping is transformed into two with ._1 and ._2 appended to the original key.
   * Can be used as a mechanism for doing "confirmation" based validation.
   */
  def confirm[T](mapping: Mapping[T], notEqualError: String = "error.equal", requiredMsg: String = "error.required"): Mapping[T] = {
    def unbind: T => String = t => mapping.unbind(t)("")

    val tupleMapping = tuple(
      "_1" -> mapping,
      "_2" -> of(string(requiredMsg))
    )

    ErrorKeyTransformMapping[(T, String)](tupleMapping, "._2", notEqualError, (o: (T, String)) => unbind(o._1) == o._2)
      .transform({
        case (t1, t2) => t1
      }, { t: T =>
        (t, unbind(t))
      })
  }
}

case class ErrorKeyTransformer(keyAffix: String)

case class ErrorKeyTransformMapping[T](wrapped: Mapping[T]) extends Mapping[T] {
  val key = wrapped.key

  val mappings = wrapped.mappings

  override val format = wrapped.format

  val constraints: Seq[Constraint[T]] = wrapped.constraints

  def bind(data: Map[String, String]): Either[Seq[FormError], T] = {
    val bound = wrapped.bind(data)
    if (bound.isLeft) {
      bound.left.map(_.map(e => {
        e.args.headOption.fold(e) {
          case transformer: ErrorKeyTransformer =>
            e.copy(key = e.key + transformer.keyAffix)
          case _ => e
        }
      }))
    } else {
      bound
    }
  }

  def unbind(value: T): Map[String, String] = wrapped.unbind(value)

  def unbindAndValidate(value: T): (Map[String, String], Seq[FormError]) = {
    wrapped.unbindAndValidate(value)
  }

  def withPrefix(prefix: String): Mapping[T] = copy(wrapped = wrapped.withPrefix(prefix))

  def verifying(constraints: Constraint[T]*): Mapping[T] = copy(wrapped = wrapped.verifying(constraints: _*))
}

object ErrorKeyTransformMapping {
  def apply[T](mapping: Mapping[T], keyAffix: => String, error: => String, constraint: (T => Boolean)): Mapping[T] = {
    this(mapping.verifying(Constraint { t: T =>
      if (constraint(t)) Valid else Invalid(Seq(ValidationError(error, ErrorKeyTransformer(keyAffix))))
    }))
  }
}
