package com.lukedeighton.play.annotation

import com.lukedeighton.play
import com.lukedeighton.play.validation.Validator

import scala.annotation.StaticAnnotation

class ValidateWith[T](value: Validator[T]) extends StaticAnnotation {
  def this(validator: (T => Boolean), errorMsg: String = "error.invalid") = {
    this(play.validation.Validator(validator, errorMsg))
  }
}