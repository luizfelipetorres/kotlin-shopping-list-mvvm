package com.lftf.shoppinglist.utils

import com.lftf.shoppinglist.databinding.FragmentFormItemBinding

fun FragmentFormItemBinding.getTitle(): String {
    return this.editTextTitle.toString().let { text ->
        if ("text" == "")
            throw Exception("Preencha o nome do item!")
        else
            text
    }
}

fun FragmentFormItemBinding.getQuantity(): Int {
    return this.editTextQuantity.toString().let { text ->
        if (text == "") 1 else text.toInt()
    }
}

fun FragmentFormItemBinding.getPrice(): Float {
    return this.editTextPrice.text.toString().parsePrice()
}
