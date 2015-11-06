package com.lukedeighton.play

class FormSpec extends BaseSpec {
  "An Empty Form" should "show the default message error.required for every Test1 parameter" in {
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

  "An Invalid Form" should "show the correct default error messages for every Test3 parameter" in {
    assert(invalidForm1.error("a").get.message == "error.number")
    assert(invalidForm1.error("b").get.message == "error.number")
    assert(invalidForm1.error("c").get.message == "error.number")
    assert(invalidForm1.error("d").get.message == "error.number")
    assert(invalidForm1.error("e").get.message == "error.real")
    assert(invalidForm1.error("f").get.message == "error.real")
  }

  "A Form with minimum values" should "show the default message error.min for every Test4 parameter" in {
    assert(minForm4.error("a").get.message == "error.min")
    assert(minForm4.error("b").get.message == "error.min")
    assert(minForm4.error("c").get.message == "error.min")
    assert(minForm4.error("d").get.message == "error.min")
    assert(minForm4.error("e").get.message == "error.min")
    assert(minForm4.error("f").get.message == "error.min")
  }

  "A Form with maximum values" should "show the default message error.min for every Test4 parameter" in {
    assert(maxForm4.error("a").get.message == "error.max")
    assert(maxForm4.error("b").get.message == "error.max")
    assert(maxForm4.error("c").get.message == "error.max")
    assert(maxForm4.error("d").get.message == "error.max")
    assert(maxForm4.error("e").get.message == "error.max")
    assert(maxForm4.error("f").get.message == "error.max")
  }

  it should "show the default message error.minLength for Test5's String parameter" in {
    assert(minForm4.error("g").get.message == "error.minLength")
  }

  it should "show the default message error.maxLength for Test5's String parameter" in {
    assert(maxForm4.error("g").get.message == "error.maxLength")
  }

  //TODO valid forms
 }
