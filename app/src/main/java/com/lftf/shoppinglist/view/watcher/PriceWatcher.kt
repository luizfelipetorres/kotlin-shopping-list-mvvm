package com.lftf.shoppinglist.view.watcher

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.lftf.shoppinglist.utils.Price

open class PriceWatcher (val field: TextInputEditText): TextWatcher {
    private var current = ""
    override fun beforeTextChanged(
        s: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString() != current) {
            field.removeTextChangedListener(this)

            val parsed = Price.parsePrice(s?.toString() ?: "")
            val formatted = Price.formatPrice(parsed)

            current = formatted
            field.setText(formatted)
            field.setSelection(formatted.length)
            field.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }
}