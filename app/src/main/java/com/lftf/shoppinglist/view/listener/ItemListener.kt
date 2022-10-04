package com.lftf.shoppinglist.view.listener

import com.lftf.shoppinglist.model.ItemModel

interface ItemListener {
    fun onLongClick(id: Int): Boolean
    fun onClick(item: ItemModel)
}