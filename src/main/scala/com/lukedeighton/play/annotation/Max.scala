package com.lukedeighton.play.annotation

class Max[T](value: T, errorMsg: String = "error.max") extends Display(errorMsg)