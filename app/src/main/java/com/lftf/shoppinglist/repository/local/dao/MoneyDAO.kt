package com.lftf.shoppinglist.repository.local.dao

import androidx.room.*
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.interfaces.IDAO
import com.lftf.shoppinglist.repository.interfaces.IRepository

@Dao
interface MoneyDAO : IDAO<MoneyModel> {

    @Insert
    override fun save(item: MoneyModel): Long

    @Query("SELECT * FROM tb_money")
    override fun listAll(): List<MoneyModel>

    @Query("SELECT * FROM tb_money WHERE ID = :id")
    override fun getOne(id: Int): MoneyModel

    @Delete
    override fun delete(item: MoneyModel)

    @Update
    override fun update(item: MoneyModel): Int

    @Insert
    fun saveAll(list: List<MoneyModel>)

    @Query("DELETE FROM tb_money")
    override fun clear()
}