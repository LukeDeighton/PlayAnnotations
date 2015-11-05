package com.lukedeighton.play.validation

abstract class Validator[T](val errorMsg: String = "error.invalid") {
  def isValid(obj: T): Boolean
}

object Validator {
  def apply[T](validator: (T => Boolean), errorMsg: String = "error.invalid"): Validator[T] = {
    new Validator[T](errorMsg) {
      override def isValid(obj: T): Boolean = validator(obj)
    }
  }
}