package com.lftf.shoppinglist.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.ItemRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    object SaveOptions{
        const val SAVED = 1
        const val UPDATED = 0
    }

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

    private fun sort(list: List<ItemModel>) {
        _list.value = list.let {
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
        _list.value?.let { sort(it) }
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
        _list.value?.let { sort(it) }
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
        sort(allItens)
        updateTotalAmount()
    }

    private fun message(msg: String) {
        _message.value = msg
        _message.value = null
    }

    fun save(item: ItemModel): Int {
        val listFiltered = _list.value?.filter { it.id == item.id }
        val isEmptyList: Boolean = listFiltered?.isEmpty() ?: true
        var returnValue: Int
        if (item.id == 0) {
            repository.save(item)
            returnValue = SaveOptions.SAVED
        } else if (isEmptyList) {
            repository.save(item)
            returnValue = SaveOptions.SAVED
        } else {
            repository.update(item)
            returnValue = SaveOptions.UPDATED
        }
        getAll()

        return returnValue

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

    fun deleteAtPosition(position: Int) {
        val id = _list.value!![position].id
        delete(id)
    }
}