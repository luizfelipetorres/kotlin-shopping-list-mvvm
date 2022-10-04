package com.lftf.shoppinglist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.ItemRepository

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ItemRepository.getInstance(application.applicationContext)
    private val _list = MutableLiveData<List<ItemModel>>()
    val listItens: LiveData<List<ItemModel>> = _list

    private val _totalAmount = MutableLiveData<Float>()
    val totalAmount: LiveData<Float> = _totalAmount


    fun getAll() {
        val allItens = repository.getItens()
        _totalAmount.value =
            allItens.map { item -> item.getTotalValue() }.reduce { acc, fl -> acc + fl }
        _list.value = allItens
    }

}