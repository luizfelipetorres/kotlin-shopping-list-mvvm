package com.lftf.shoppinglist.repository.local

import android.content.Context
import com.lftf.shoppinglist.data.DatabaseHelper
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.interfaces.IItemRepository

class ItemRepository(
    context: Context
) : RoomRepository<ItemModel>(DatabaseHelper.getInstance(context).itemDao()), IItemRepository