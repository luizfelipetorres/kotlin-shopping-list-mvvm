package com.lftf.shoppinglist.repository

import com.lftf.shoppinglist.model.AbstractModel
import com.lftf.shoppinglist.repository.interfaces.IRepository

abstract class AbstractRepository<T : AbstractModel>() : IRepository<T>