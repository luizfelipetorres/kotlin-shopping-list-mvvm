package com.lftf.shoppinglist.repository.interfaces

import com.lftf.shoppinglist.model.MoneyModel

interface IMoneyRepository : IRepository<MoneyModel> {

    fun saveAll(list: List<MoneyModel>)
    fun clear()
}