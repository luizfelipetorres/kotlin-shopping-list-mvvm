package com.lftf.shoppinglist.repository.interfaces

import androidx.room.Dao
import com.lftf.shoppinglist.model.AbstractModel

@Dao
interface IDAO<T : AbstractModel> {

    fun save(item: T): Long

    fun listAll(): List<T>

    fun getOne(id: Int): T

    fun delete(item: T)

    fun update(item: T): Int

    fun clear()
}