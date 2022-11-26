package com.lftf.shoppinglist.repository

import com.lftf.shoppinglist.model.AbstractModel
import com.lftf.shoppinglist.repository.interfaces.IDAO
import com.lftf.shoppinglist.repository.interfaces.IRepository

abstract class AbstractRepository<T : AbstractModel>(protected val dao: IDAO<T>) : IRepository<T> {

    override fun save(item: T): Boolean = dao.save(item) == 1L

    override fun listAll(): List<T> = dao.listAll()

    override fun getOne(id: Int): T = dao.getOne(id)

    override fun delete(id: Int) = dao.delete(dao.getOne(id))

    override fun update(item: T): Boolean = dao.update(item) == 1

    open fun clear(){

    }
}