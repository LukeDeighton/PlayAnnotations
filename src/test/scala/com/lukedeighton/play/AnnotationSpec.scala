package com.lukedeighton.play

class AnnotationSpec extends BaseSpec {
  /* Required */
  "An Empty Form" should "show a custom error message for every Test3 parameter" in {
    assert(emptyForm3.error("a").get.message == "REQUIRED A")
    assert(emptyForm3.error("b").get.message == "REQUIRED B")
    assert(emptyForm3.error("c").get.message == "REQUIRED C")
    assert(emptyForm3.error("d").get.message == "REQUIRED D")
    assert(emptyForm3.error("e").get.message == "REQUIRED E")
    assert(emptyForm3.error("f").get.message == "REQUIRED F")
    assert(emptyForm3.error("g").get.message == "REQUIRED G")
  }

  /* Invalid */
  "An Invalid Form" should "show a custom invalid message for every Test3 parameter" in {
    assert(invalidForm3.error("a").get.message == "INVALID A")
    assert(invalidForm3.error("b").get.message == "INVALID B")
    assert(invalidForm3.error("c").get.message == "INVALID C")
    assert(invalidForm3.error("d").get.message == "INVALID D")
    assert(invalidForm3.error("e").get.message == "INVALID E")
    assert(invalidForm3.error("f").get.message == "INVALID F")
  }

  /* Min */
  "A Form with minimum values" should "show a custom min message for every Test5 parameter" in {
    assert(minForm5.error("a").get.message == "MIN A")
    assert(minForm5.error("b").get.message == "MIN B")
    assert(minForm5.error("c").get.message == "MIN C")
    assert(minForm5.error("d").get.message == "MIN D")
    assert(minForm5.error("e").get.message == "MIN E")
    assert(minForm5.error("f").get.message == "MIN F")
  }

  /* Max */
  "A Form with maximum values" should "show a custom max message for every Test5 parameter" in {
    assert(maxForm5.error("a").get.message == "MAX A")
    assert(maxForm5.error("b").get.message == "MAX B")
    assert(maxForm5.error("c").get.message == "MAX C")
    assert(maxForm5.error("d").get.message == "MAX D")
    assert(maxForm5.error("e").get.message == "MAX E")
    assert(maxForm5.error("f").get.message == "MAX F")
  }

  /* MinLength */
  "A Form with minimum length values" should "show a custom min length message for Test5's String parameter" in {
    assert(minForm5.error("g").get.message == "MIN G")
  }

  /* MaxLength */
  "A Form with maximum length values" should "show a custom max length message for Test5's String parameter" in {
    assert(maxForm5.error("g").get.message == "MAX G")
  }

  /* Email */
  "A Form with an email annotated field" should "accept test@test.com" in {
    val validForm = test6Form.bind(Map("a" -> "test@test.com"))
    assert(!validForm.hasErrors)
  }

  it should "not accept test@test" in {
    val validForm = test6Form.bind(Map("a" -> "test"))
    assert(validForm.hasErrors)
  }

  it should "have a custom error message for Test6's parameter" in {
    val invalidForm = test6Form.bind(Map("a" -> "test"))
    assert(invalidForm.error("a").get.message == "EMAIL A")
  }

  /* Pattern */
  "A Form with a pattern annotated field [abc]{5,8}" should "accept aabbcc" in {
    val validForm = test7Form.bind(Map("a" -> "aabbcc"))
    assert(!validForm.hasErrors)
  }

  it should "not accept aabb" in {
    val invalidForm = test7Form.bind(Map("a" -> "aabb"))
    assert(invalidForm.hasErrors)
  }

  it should "not accept bbccdd" in {
    val invalidForm = test7Form.bind(Map("a" -> "bbccdd"))
    assert(invalidForm.hasErrors)
  }

  it should "have a custom error message for Test6's b paramter" in {
    val validForm = test7Form.bind(Map("a" -> "bbccdd"))
    assert(validForm.error("a").get.message == "PATTERN A")
  }

  /* ValidateWith */
}
