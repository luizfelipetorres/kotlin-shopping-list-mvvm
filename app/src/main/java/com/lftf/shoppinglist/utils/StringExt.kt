package com.lftf.shoppinglist.utils

private val regexPrice: Regex = """[\sR,$.]""".toRegex()

/**
 * Retorna o float da String preço ou '0f' em caso de erro
 */
fun String.parsePrice(): Float {
    val stringParsed = this.replace(regexPrice, "")
    return try {
        stringParsed.let { if (it == "") 0f else it.toFloat() / 100 }
    } catch (e: NumberFormatException) {
        0f
    }
}
