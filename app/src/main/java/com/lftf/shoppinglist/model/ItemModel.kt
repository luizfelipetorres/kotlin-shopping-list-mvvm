package com.lftf.shoppinglist.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_item")
class ItemModel{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var id: Int = 0

    @ColumnInfo
    var title: String = ""

    @ColumnInfo
    var quantity: Int = 1

    @ColumnInfo
    var price: Float = 0f

    fun getTotalValue(): Float = price * quantity
}