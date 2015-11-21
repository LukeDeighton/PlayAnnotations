package com.lukedeighton.play.validation

import com.lukedeighton.play.mcros
import play.api.data.validation.{Valid, ValidationError, Invalid, Constraint}
import play.api.data.{FormError, Mapping, Form}

import scala.language.experimental.macros

object Forms {
  //ignore import warnings in Intellij - the Macro Bundle works fine!
  def form[T]: Form[T] = macro mcros.FormImpl.formExpr[T]

  //TODO how would you add error overrides per type? - implicit settings? this could be a way to use injector?

  //TODO other macro impls?
  //def form[T](obj: T) = form[T].fill(obj)

  //implicit def toForm[T](request: Request[_]): Form[T] = form[T].bindFromRequest()(request)
}