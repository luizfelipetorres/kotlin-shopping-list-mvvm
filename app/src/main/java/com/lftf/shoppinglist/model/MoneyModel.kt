package com.lftf.shoppinglist.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_money")
class MoneyModel() : AbstractModel() {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    override var id: Int = 0

    @ColumnInfo
    var method: String = ""

    @ColumnInfo
    var limit: Float = 0f

}