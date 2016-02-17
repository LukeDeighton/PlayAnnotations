package com.lukedeighton.play

import com.lukedeighton.play.annotation._

case class Test1(a: Long, b: Int, c: Short, d: Byte, e: Float, f: Double, g: String)

case class Test2(a: Option[String], b: Option[Int])

case class Test3(
  @Required("REQUIRED A")
  @Invalid("INVALID A")
  a: Long,

  @Required("REQUIRED B")
  @Invalid("INVALID B")
  b: Int,

  @Required("REQUIRED C")
  @Invalid("INVALID C")
  c: Short,

  @Required("REQUIRED D")
  @Invalid("INVALID D")
  d: Byte,

  @Required("REQUIRED E")
  @Invalid("INVALID E")
  e: Float,

  @Required("REQUIRED F")
  @Invalid("INVALID F")
  f: Double,

  @Required("REQUIRED G")
  g: String
)

case class Test4(
  @Min(10)
  @Max(15)
  a: Long,

  @Min(10)
  @Max(15)
  b: Int,

  @Min(10)
  @Max(15)
  c: Short,

  @Min(10)
  @Max(15)
  d: Byte,

  @Min(10)
  @Max(15)
  e: Float,

  @Min(10)
  @Max(15)
  f: Double,

  @MinLength(10)
  @MaxLength(15)
  g: String
)

case class Test5(
  @Min(10, "MIN A")
  @Max(15, "MAX A")
  a: Long,

  @Min(10, "MIN B")
  @Max(15, "MAX B")
  b: Int,

  @Min(10, "MIN C")
  @Max(15, "MAX C")
  c: Short,

  @Min(10, "MIN D")
  @Max(15, "MAX D")
  d: Byte,

  @Min(10, "MIN E")
  @Max(15, "MAX E")
  e: Float,

  @Min(10, "MIN F")
  @Max(15, "MAX F")
  f: Double,

  @MinLength(10, "MIN G")
  @MaxLength(15, "MAX G")
  g: String
)

case class Test6(
  @Email("EMAIL A")
  a: String
)

case class Test7(
  @Pattern("[abc]{5,8}", "PATTERN A")
  a: String
)