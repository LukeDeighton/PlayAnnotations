# PlayAnnotations

A Scala library that uses macros to generate Play Framework form validation code from case classes. Form validation feels convoluted in the Play framework, you must define an `ObjectMapping` with `FieldMapping`s that match one to one with each field within your case class. The field `Mapping`s must be in the same order as defined in the case class and also provide an `apply` and `unapply` method. The validation API is also not very flexible to customisation, for example changing the error message for a given mapping is not straight forward. You may need to provide a new binder (`Formatter`) to make a simple change and in most cases out of the box form validation is not reuseable. PlayAnnotations provides a more convenient way to handle validation and is more similar to Java based approaches by decorating each case class field with annotations.

## Example

1) define a case class
```scala
import com.lukedeighton.play.annotation._

case class Signup(
  @MaxLength(45, "The Forename must be 45 or less characters long")
  forename: String,

  @MaxLength(45, "The Surname must be 45 or less characters long")
  surname: String,

  @MaxLength(255, "The Password must be 255 or less characters long")
  @Confirm("Password and Confirm Password must match")
  password: String,

  @MaxLength(45, "The Email must be 45 or less characters long")
  @Email("You must provide a valid email address")
  email: String)
```

2) use a macro to convert from a type `T` into a `Form[T]`
```scala
import com.lukedeighton.play.validation.Forms.form
val form: Form[Signup] = form[Signup]
```

The library does the magic of converting your case class into scala code based on the annotations of each field. Note that annotations are optional and the library has default validation handling based on the type of each field. The example above will produce AST and equivalent source code after macro expansion:
```scala
{
  import play.api.data.Form;
  import play.api.data.Forms._;
  import com.lukedeighton.play.validation._;
  import com.lukedeighton.play.validation.Binders._;
  import com.lukedeighton.play.validation.Mappings._;
  Form(
    mapping(
      "forename".$minus$greater(of(string()).verifying(Constraints.maxLength(45, "The Forename must be 45 or less characters long"))),
      "surname".$minus$greater(of(string()).verifying(Constraints.maxLength(45, "The Surname must be 45 or less characters long"))),
      "password".$minus$greater(confirm(of(string()).verifying(Constraints.maxLength(255, "The Password must be 255 or less characters long")), "Password and Confirm Password must match")),
      "email".$minus$greater(of(string()).verifying(Constraints.maxLength(45, "The Email must be 45 or less characters long")).verifying(Constraints.emailAddress("You must provide a valid email address")))
    )(Signup.apply)(Signup.unapply))
}
```

## Annotations

| Name            | Supported Field Type                              | Example                                           |
|-----------------|---------------------------------------------------|---------------------------------------------------|
| Max             | `Int`, `Long`, `Float`, `Double`, `Short`, `Byte` | `@Max(25, "errMsg")`                              |
| Min             | `Int`, `Long`, `Float`, `Double`, `Short`, `Byte` | `@Min(15, "errMsg")`                              |
| MaxLength       | `String`                                          | `@MaxLength(25, "errMsg")`                        |
| MinLength       | `String`                                          | `@MinLength(15, "errMsg")`                        |
| Email           | `String`                                          | `@Email("errMsg")`                                |
| Pattern (Regex) | `String`                                          | `@Pattern("[abc]{5,8}", "errMsg")`                |
| Confirm         | `Any`                                             | `@Confirm("errMsg")`                              |
| Required        | `Any`                                             | `@Required("errMsg")`                             |
| Invalid         | `Int`, `Long`, `Float`, `Double`, `Short`, `Byte` | `@Invalid("errMsg")`                              |
| ValidateWith    | `Any`                                             | `@ValidateWith((b: Int) => b % 2 == 0, "errMsg")` |
