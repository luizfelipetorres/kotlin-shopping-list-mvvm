package com.lftf.shoppinglist.view.watcher

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.lftf.shoppinglist.utils.formatPrice
import com.lftf.shoppinglist.utils.parsePrice

open class PriceWatcher(val field: EditText) : TextWatcher {
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

            val parsed = s?.toString()?.parsePrice() ?: 0f
            val formatted = parsed.formatPrice()

            current = formatted
            field.setText(formatted)
            field.setSelection(formatted.length)
            field.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }
}