package com.lftf.shoppinglist.repository.interfaces

import com.lftf.shoppinglist.model.AbstractModel

interface IRepository<T: AbstractModel> {

    fun save(item: T): Boolean

    fun listAll(): List<T>

    fun getOne(id: Int): T

    fun delete(id: Int)

    fun update(item: T): Boolean
}