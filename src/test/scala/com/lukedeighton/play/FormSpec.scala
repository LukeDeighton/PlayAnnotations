package com.lukedeighton.play

import com.lukedeighton.play.validation.Forms._
import org.scalatest.FlatSpec

case class Test1(a: Long, b: Int, c: Short, d: Byte, e: Float, f: Double, g: String)

case class Test2(a: Option[String], b: Option[Int])

class FormSpec extends FlatSpec {
   val test1Form = form[Test1]
   val test2Form = form[Test2]

   val emptyMap = Map.empty[String, String]
   val emptyForm1 = test1Form.bind(emptyMap)
   val emptyForm2 = test2Form.bind(emptyMap)

   "An Empty Form" should "show error.required for every Test1 parameter" in {
     for (e <- emptyForm1.errors) {
       assert(e.message == "error.required")
     }
   }

   it should "contain 7 errors for Test1" in {
     assert(emptyForm1.errors.length == 7)
   }

   it should "have an error for every Test1 parameter" in {
     assert(emptyForm1.error("a").isDefined)
     assert(emptyForm1.error("b").isDefined)
     assert(emptyForm1.error("c").isDefined)
     assert(emptyForm1.error("d").isDefined)
     assert(emptyForm1.error("e").isDefined)
     assert(emptyForm1.error("f").isDefined)
     assert(emptyForm1.error("g").isDefined)
   }

   it should "contain no errors for Test2" in {
     assert(emptyForm2.errors.isEmpty)
   }
 }
