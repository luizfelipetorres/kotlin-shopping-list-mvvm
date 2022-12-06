package com.lftf.shoppinglist.utils

import java.text.NumberFormat
import java.util.*

private val regexPrice: Regex = """[\sR,$.]""".toRegex()

fun String.parsePrice(): Float {
    val stringParsed = this.replace(regexPrice, "")
    return stringParsed.let { if (it == "") 0f else it.toFloat() / 100 }
}

fun Float.formatPrice(): String {
    val lBrazil = Locale("pt", "BR")
    return NumberFormat.getCurrencyInstance(lBrazil).format((this))
}
