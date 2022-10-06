package com.lftf.shoppinglist.repository

import android.content.Context
import com.lftf.shoppinglist.data.DatabaseHelper
import com.lftf.shoppinglist.model.ItemModel

class ItemRepository(context: Context) {

    private val itemDatabase = DatabaseHelper.getInstance(context).itemDao()

    fun save(item: ItemModel): Boolean = itemDatabase.save(item) == 1.toLong()

    fun getItens(): List<ItemModel> = itemDatabase.getItens()

    fun delete(id: Int): Boolean = itemDatabase.delete(itemDatabase.getItem(id)) == 1

    fun update(item: ItemModel): Boolean = itemDatabase.update(item) == 1

}