package com.lftf.shoppinglist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.ItemRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ItemRepository(application.applicationContext)

    private val _list = MutableLiveData<List<ItemModel>>()
    val list: LiveData<List<ItemModel>> = _list

    private var _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _totalAmount = MutableLiveData<Float>()
    val totalAmount: LiveData<Float> = _totalAmount

    private val _lastItem = MutableLiveData<ItemModel?>()
    val lastItem: LiveData<ItemModel?> = _lastItem

    private val _sortPrice = MutableLiveData<Boolean?>()
    val sortPrice: LiveData<Boolean?> = _sortPrice

    private val _sortTitle = MutableLiveData<Boolean?>()
    val sortTitle: LiveData<Boolean?> = _sortTitle

    fun updateLastItem(item: ItemModel?) {
        _lastItem.value = item
    }

    private fun sort() {
        _list.value = _list.value?.let {
            if (_sortPrice.value != null) {
                when (_sortPrice.value) {
                    true -> it.sortedBy { item -> item.getTotalValue() }
                    else -> it.sortedByDescending { item -> item.getTotalValue() }
                }
            } else if (_sortTitle.value != null) {
                when (_sortTitle.value) {
                    true -> it.sortedBy { item -> item.title }
                    else -> it.sortedByDescending { item -> item.title }
                }
            } else {
                it.sortedBy { item -> item.id }
            }
        }
    }

    private fun changeSort(value: Boolean?): Boolean? = when (value) {
        null -> true
        true -> false
        else -> null
    }

    fun changeSortPrice() {
        _sortPrice.value = _sortPrice.value.let {
            changeSort(it)?.also { res ->
                message("Ordenando preço por ordem ${if (res) "crescente" else "decrescente"}")
            }
        }
        if (_sortTitle.value != null) {
            _sortTitle.value = null
        }
        sort()
    }

    fun changeSortTitle() {
        _sortTitle.value = _sortTitle.value.let {
            changeSort(it)?.also { res ->
                message("Ordenando título por ordem alfabética ${if (res) "" else "inversa"}")
            }
        }
        if (_sortPrice.value != null) {
            _sortPrice.value = null
        }
        sort()
    }

    private fun updateTotalAmount() {
        _totalAmount.value = _list.value?.toMutableList()?.let {
            if (it.isEmpty())
                0f
            else
                it.map { i -> i.getTotalValue() }.reduce { previous, current -> previous + current }
        }
    }

    fun getAll() {
        val allItens = repository.getItens()
        _list.value = allItens
        updateTotalAmount()
        sort()
    }

    private fun message(msg: String) {
        _message.value = msg
        _message.value = null
    }

    fun save(item: ItemModel) {
        if (item.id == 0)
            repository.save(item)
        else {
            repository.update(item)
        }.also {
            getAll()
        }
    }

    fun delete(id: Int) {
        repository.delete(id).let {
            if (it)
                message("Excluído com sucesso!")
            else
                message("Falha ao excluir")
        }.also {
            getAll()
        }
    }
}