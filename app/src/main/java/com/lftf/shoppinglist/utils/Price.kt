package com.lftf.shoppinglist.utils

import java.text.NumberFormat
import java.util.*

class Price private constructor() {
    companion object {
        private val regexPrice: Regex = """[\sR,$.]""".toRegex()

        fun parsePrice(stringPrice: String): Float {
            val stringPrice = stringPrice
            val stringParsed = stringPrice.replace(regexPrice, "")
            return stringParsed.let { if (it == "") 0f else it.toFloat() / 100 }
        }

        fun formatPrice(floatPrice: Float): String {
            val lBrazil = Locale("pt", "BR")
            return NumberFormat.getCurrencyInstance(lBrazil).format((floatPrice))
        }
    }
}