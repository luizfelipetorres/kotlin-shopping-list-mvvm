package com.lftf.shoppinglist.repository

import androidx.room.*
import com.lftf.shoppinglist.model.ItemModel

@Dao
interface ItemDAO {

    @Insert
    fun save(item: ItemModel): Long

    @Query("SELECT * FROM tb_item")
    fun getItens(): List<ItemModel>

    @Query("SELECT * FROM tb_item WHERE ID = :id")
    fun getItem(id:Int): ItemModel

    @Delete
    fun delete(item: ItemModel): Int

    @Update
    fun update(item: ItemModel): Int
}