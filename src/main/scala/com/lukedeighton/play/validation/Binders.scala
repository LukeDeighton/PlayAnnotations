package com.lukedeighton.play.validation

import play.api.data.FormError
import play.api.data.format.Formatter

import scala.util.{Failure, Success, Try}

object Binders {
  def string(requiredMsg: String = "error.required"): Formatter[String] = new StringFormatter(requiredMsg)

  def long(invalidMsg: String = "error.number", requiredMsg: String = "error.required"): Formatter[Long] =
    new NumberFormatter[Long](_.toLong, invalidMsg, requiredMsg)

  def int(invalidMsg: String = "error.number", requiredMsg: String = "error.required"): Formatter[Int] =
    new NumberFormatter[Int](_.toInt, invalidMsg, requiredMsg)

  def short(invalidMsg: String = "error.number", requiredMsg: String = "error.required"): Formatter[Short] =
    new NumberFormatter[Short](_.toShort, invalidMsg, requiredMsg)

  def byte(invalidMsg: String = "error.number", requiredMsg: String = "error.required"): Formatter[Byte] =
    new NumberFormatter[Byte](_.toByte, invalidMsg, requiredMsg)

  def float(invalidMsg: String = "error.real", requiredMsg: String = "error.required"): Formatter[Float] =
    new NumberFormatter[Float](_.toFloat, invalidMsg, requiredMsg)

  def double(invalidMsg: String = "error.real", requiredMsg: String = "error.required"): Formatter[Double] =
    new NumberFormatter[Double](_.toDouble, invalidMsg, requiredMsg)
}

abstract class AbsFormatter[A](requiredMsg: String = "error.required", trim: Boolean = true) extends Formatter[A] {
  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] = {
    data.get(key).fold(error(requiredMsg)) { value =>
      val v = if (trim) value.trim else value
      if (v.isEmpty) error(requiredMsg) else bind(v)
    }(key)
  }

  override def unbind(key: String, value: A): Map[String, String] = {
    Map(key -> unbind(value))
  }

  def bind(value: String): String => Either[Seq[FormError], A]

  def unbind(value: A): String

  def success(value: A): String => Either[Seq[FormError], A] = {
    key => Right(value)
  }

  def error(errorMsg: String, args: Any*): String => Either[Seq[FormError], A] = {
    key => Left(Seq(FormError(key, errorMsg, args.toSeq)))
  }
}

class NumberFormatter[A](convert: String => A,
                         invalidMsg: String = "error.number",
                         requiredMsg: String = "error.required") extends AbsFormatter[A](requiredMsg) {
  override def bind(value: String): (String) => Either[Seq[FormError], A] = {
    Try(convert(value)) match {
      case Success(i) =>
        success(i)
      case Failure(_) =>
        error(invalidMsg)
    }
  }

  override def unbind(value: A): String = value.toString
}

class StringFormatter(requiredMsg: String = "error.required",
                      trim: Boolean = true) extends AbsFormatter[String](requiredMsg, trim) {
  override def bind(value: String) = success(value)

  override def unbind(value: String) = value
}