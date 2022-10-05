package com.lftf.shoppinglist.view.listener

import com.lftf.shoppinglist.model.ItemModel

interface ItemListener {
    fun onLongClick(id: Int, callback: () -> Unit): Boolean
    fun onClick(item: ItemModel)
}