package com.lukedeighton.play

import com.lukedeighton.play.validation.Forms._
import org.scalatest.FlatSpec

class BaseSpec extends FlatSpec {
  lazy val test1Form = form[Test1]
  lazy val test2Form = form[Test2]
  lazy val test3Form = form[Test3]
  lazy val test4Form = form[Test4]
  lazy val test5Form = form[Test5]

  lazy val emptyMap = Map.empty[String, String]
  lazy val emptyForm1 = test1Form.bind(emptyMap)
  lazy val emptyForm2 = test2Form.bind(emptyMap)
  lazy val emptyForm3 = test3Form.bind(emptyMap)

  lazy val invalidMap = Map("a" -> "x", "b" -> "x", "c" -> "x", "d" -> "x", "e" -> "x", "f" -> "x")
  lazy val invalidForm1 = test1Form.bind(invalidMap)
  lazy val invalidForm3 = test3Form.bind(invalidMap)

  lazy val minMap = Map("a" -> "5", "b" -> "5", "c" -> "5", "d" -> "5", "e" -> "5", "f" -> "5", "g" -> "a" * 5)
  lazy val minForm4 = test4Form.bind(minMap)
  lazy val minForm5 = test5Form.bind(minMap)

  lazy val maxMap = Map("a" -> "25", "b" -> "25", "c" -> "25", "d" -> "25", "e" -> "25", "f" -> "25", "g" -> "a" * 25)
  lazy val maxForm4 = test4Form.bind(maxMap)
  lazy val maxForm5 = test5Form.bind(maxMap)
}
