package com.lftf.shoppinglist.repository.local.dao

import androidx.room.*
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.interfaces.IDAO

@Dao
interface ItemDAO : IDAO<ItemModel> {

    @Insert
    override fun save(item: ItemModel): Long

    @Query("SELECT * FROM tb_item")
    override fun listAll(): List<ItemModel>

    @Query("SELECT * FROM tb_item WHERE ID = :id")
    override fun getOne(id: Int): ItemModel

    @Delete
    override fun delete(item: ItemModel)

    @Update
    override fun update(item: ItemModel): Int

    @Query("DELETE FROM tb_item")
    override fun clear()
}