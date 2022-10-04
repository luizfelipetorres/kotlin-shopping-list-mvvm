package com.lftf.shoppinglist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.ItemRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ItemRepository.getInstance(application.applicationContext)
    private val _list = MutableLiveData<List<ItemModel>>()
    val listItens: LiveData<List<ItemModel>> = _list
    private var msgString = MutableLiveData<String>()
    private val _totalAmount = MutableLiveData<Float>()
    val totalAmount: LiveData<Float> = _totalAmount


    fun getAll() {
        val allItens = repository.getItens()
        allItens.let {
            if (it.isEmpty())
                0f
            else
                it.map { i -> i.getTotalValue() }.reduce { previous, current -> previous + current }

        }
        _totalAmount.value = allItens.let {
            if (it.isEmpty())
                0f
            else
                it.map { i -> i.getTotalValue() }.reduce { previous, current -> previous + current }
        }

        _list.value = allItens
    }

    fun message(msg: String) {
        msgString.value = msg
    }

    fun message(): LiveData<String> = msgString

    fun save(item: ItemModel) {
        repository.save(item)
        getAll()
    }

    fun delete(id: Int) {
        repository.delete(id)
        getAll()
    }
}