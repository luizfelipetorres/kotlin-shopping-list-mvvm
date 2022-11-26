package com.lftf.shoppinglist.repository.local

import android.content.Context
import com.lftf.shoppinglist.data.DatabaseHelper
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.AbstractRepository

class ItemRepository(
    context: Context
) : AbstractRepository<ItemModel>(DatabaseHelper.getInstance(context).itemDao())