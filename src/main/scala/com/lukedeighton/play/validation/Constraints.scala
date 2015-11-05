package com.lukedeighton.play.validation

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

/**
 * Copied and modified from Play Framework to allow changeable error messages not from the message file.
 */
object Constraints {

  /**
   * Defines an ‘emailAddress’ constraint for `String` values which will validate email addresses.
   *
   * '''name'''[constraint.email]
   * '''error'''[error.email]
   */
  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
  def emailAddress(error: String = "error.email"): Constraint[String] = Constraint[String]("constraint.email") { e =>
    if (e == null) Invalid(ValidationError(error))
    else if (e.trim.isEmpty) Invalid(ValidationError(error))
    else emailRegex.findFirstMatchIn(e)
      .map(_ => Valid)
      .getOrElse(Invalid(ValidationError(error)))
  }

  /**
   * Defines a ‘required’ constraint for `String` values, i.e. one in which empty strings are invalid.
   *
   * '''name'''[constraint.required]
   * '''error'''[error.required]
   */
  def nonEmpty(error: String = "error.required"): Constraint[String] = Constraint[String]("constraint.required") { o =>
    if (o == null) Invalid(ValidationError(error)) else if (o.trim.isEmpty) Invalid(ValidationError(error)) else Valid
  }

  /**
   * Defines a minimum value for `Ordered` values, by default the value must be greater than or equal to the constraint parameter
   *
   * '''name'''[constraint.min(minValue)]
   * '''error'''[error.min(minValue)] or [error.min.strict(minValue)]
   */
  def min[T](minValue: T, error: String = "error.min")(implicit ordering: scala.math.Ordering[T]): Constraint[T] = Constraint[T]("constraint.min", minValue) { o =>
    ordering.compare(o, minValue).signum match {
      case 1 | 0 => Valid
      case _ => Invalid(ValidationError(error, minValue))
    }
  }

  /**
   * Defines a maximum value for `Ordered` values, by default the value must be less than or equal to the constraint parameter
   *
   * '''name'''[constraint.max(maxValue)]
   * '''error'''[error.max(maxValue)] or [error.max.strict(maxValue)]
   */
  def max[T](maxValue: T, error: String = "error.max")(implicit ordering: scala.math.Ordering[T]): Constraint[T] = Constraint[T]("constraint.max", maxValue) { o =>
    ordering.compare(o, maxValue).signum match {
      case -1 | 0 => Valid
      case _ => Invalid(ValidationError(error, maxValue))
    }
  }

  /**
   * Defines a minimum length constraint for `String` values, i.e. the string’s length must be greater than or equal to the constraint parameter
   *
   * '''name'''[constraint.minLength(length)]
   * '''error'''[error.minLength(length)]
   */
  def minLength(length: Int, error: String = "error.minLength"): Constraint[String] = Constraint[String]("constraint.minLength", length) { o =>
    require(length >= 0, "string minLength must not be negative")
    if (o == null) Invalid(ValidationError(error, length)) else if (o.length >= length) Valid else Invalid(ValidationError(error, length))
  }

  /**
   * Defines a maximum length constraint for `String` values, i.e. the string’s length must be less than or equal to the constraint parameter
   *
   * '''name'''[constraint.maxLength(length)]
   * '''error'''[error.maxLength(length)]
   */
  def maxLength(length: Int, error: String = "error.maxLength"): Constraint[String] = Constraint[String]("constraint.maxLength", length) { o =>
    require(length >= 0, "string maxLength must not be negative")
    if (o == null) Invalid(ValidationError(error, length)) else if (o.length <= length) Valid else Invalid(ValidationError(error, length))
  }

  /**
   * Defines a regular expression constraint for `String` values, i.e. the string must match the regular expression pattern
   *
   * '''name'''[constraint.pattern(regex)] or defined by the name parameter.
   * '''error'''[error.pattern(regex)] or defined by the error parameter.
   */
  def pattern(regex: => scala.util.matching.Regex, name: String = "constraint.pattern", error: String = "error.pattern"): Constraint[String] = Constraint[String](name, () => regex) { o =>
    require(regex != null, "regex must not be null")
    require(name != null, "name must not be null")
    require(error != null, "error must not be null")

    if (o == null) Invalid(ValidationError(error, regex)) else regex.unapplySeq(o).map(_ => Valid).getOrElse(Invalid(ValidationError(error, regex)))
  }

}
