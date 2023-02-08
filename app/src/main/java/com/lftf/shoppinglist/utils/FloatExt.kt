package com.lftf.shoppinglist.utils

import java.text.NumberFormat
import java.util.*


/**
 * Retorna a String em formato de real
 */
fun Float.formatPrice(): String {
    val lBrazil = Locale("pt", "BR")
    return NumberFormat.getCurrencyInstance(lBrazil).format((this))
}
