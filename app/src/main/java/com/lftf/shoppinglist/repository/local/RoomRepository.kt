package com.lftf.shoppinglist.repository.local

import com.lftf.shoppinglist.model.AbstractModel
import com.lftf.shoppinglist.repository.AbstractRepository
import com.lftf.shoppinglist.repository.interfaces.IDAO

abstract class RoomRepository<T : AbstractModel>(val dao: IDAO<T>) :
    AbstractRepository<T>() {

    override fun save(item: T): Boolean = dao.save(item) == 1L

    override fun listAll(): List<T> = dao.listAll()

    override fun getOne(id: Int): T = dao.getOne(id)

    override fun delete(id: Int) = dao.getOne(id).let { dao.delete(it) }

    override fun update(item: T): Boolean = dao.update(item) == 1
}