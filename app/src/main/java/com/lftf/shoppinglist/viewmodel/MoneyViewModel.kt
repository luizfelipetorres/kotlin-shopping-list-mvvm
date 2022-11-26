package com.lftf.shoppinglist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.local.MoneyRepository

class MoneyViewModel(private val repository: MoneyRepository) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        class Factory(val repository: MoneyRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MoneyViewModel(repository) as T
            }
        }
    }

    private val _list: MutableLiveData<List<MoneyModel>> = MutableLiveData<List<MoneyModel>>()
    val list: LiveData<List<MoneyModel>> = _list

    private val _deletedPosition = MutableLiveData<Int>()
    val deletedPosition: LiveData<Int> = _deletedPosition

    fun deleteAtPosition(position: Int) {
        val id = _list.value!![position].id
        delete(id)
        _deletedPosition.value = position
    }

    fun delete(id: Int) {
        repository.delete(id)
        getAll()
    }

    fun saveAll() {
//        TODO("Salvar as informações em _list antes de salvar no BD")

        _list.value?.let { lista ->
            repository.clear().also {
                repository.saveAll(lista)
            }
        }
    }

    fun getAll() {
        val lista = repository.listAll().ifEmpty { mutableListOf(MoneyModel()) }
        _list.value = lista

    }

    fun addToList() {
        repository.save(MoneyModel())
        getAll()
    }

}