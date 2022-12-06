package com.lftf.shoppinglist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.local.MoneyRepository

class MoneyViewModel(private val repository: MoneyRepository) : ViewModel() {

    private val _list: MutableLiveData<List<MoneyModel>> = MutableLiveData<List<MoneyModel>>()
    val list: LiveData<List<MoneyModel>> = _list

    fun deleteAtPosition(position: Int) {
        val id = _list.value!![position].id
        delete(id)
    }

    fun delete(id: Int) {
        repository.delete(id)
        getAll()
    }

    fun saveAll(lastEmpty: Boolean = false) {
        _list.value?.let { lista ->
            repository.clear().also {
                val newList = mutableListOf<MoneyModel>()
                lista.forEach { e -> newList.add(e) }
                    .also { if (lastEmpty) newList.add(MoneyModel()) }
                repository.saveAll(newList)
            }
        }
    }

    fun getAll() {
        repository.listAll().let { _list.value = it }
    }

    fun addToList() {
        saveAll(lastEmpty = true)
        getAll()
    }

    fun save(item: MoneyModel) {
        val newList = mutableListOf<MoneyModel>()
        _list.value?.let { newList.addAll(it) }
        newList.add(item)
        _list.value = newList
        saveAll()
    }
}