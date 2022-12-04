package com.lftf.shoppinglist.repository.local

import android.content.Context
import com.lftf.shoppinglist.data.DatabaseHelper
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.AbstractRepository
import com.lftf.shoppinglist.repository.interfaces.IMoneyRepository

class MoneyRepository(
    context: Context
) : AbstractRepository<MoneyModel>(DatabaseHelper.getInstance(context).moneyDao()),
    IMoneyRepository {

    private val db = DatabaseHelper.getInstance(context).moneyDao()

    override fun save(item: MoneyModel): Boolean = db.save(item) == 1L

    override fun saveAll(list: List<MoneyModel>) = db.saveAll(list)

    override fun clear() {
        db.clear()
    }
}