package com.lftf.shoppinglist.view.listener

import com.lftf.shoppinglist.model.ItemModel

interface ItemListener {
    fun onClick(item: ItemModel)
}