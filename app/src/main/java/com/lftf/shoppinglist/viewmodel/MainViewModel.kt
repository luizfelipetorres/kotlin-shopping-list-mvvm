package com.lftf.shoppinglist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.ItemRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    enum class SortOptions {
        PRICE,
        TITLE
    }

    val string: String = "teste"
    private val repository = ItemRepository.getInstance(application.applicationContext)
    private val _list = MutableLiveData<List<ItemModel>>()
    val listItens: LiveData<List<ItemModel>> = _list

    private var msgString = MutableLiveData<String?>()
    private val _totalAmount = MutableLiveData<Float>()
    val totalAmount: LiveData<Float> = _totalAmount
    private val _lastItem = MutableLiveData<ItemModel?>()

    private val _sortPrice = MutableLiveData<Boolean?>()
    private val _sortTitle = MutableLiveData<Boolean?>()


    fun lastItem(): LiveData<ItemModel?> = _lastItem
    fun getSortPrice(): LiveData<Boolean?> = _sortPrice
    fun getSortTitle(): LiveData<Boolean?> = _sortTitle

    fun updateLastItem(item: ItemModel?) {
        _lastItem.value = item
    }

    private fun sort(sortedBy: SortOptions?) {
        _list.value = when (sortedBy) {
            SortOptions.PRICE -> {
                _list.value?.let {
                    when (_sortPrice.value) {
                        true -> it.sortedBy { itemModel -> itemModel.getTotalValue() }
                        false -> it.sortedByDescending { itemModel -> itemModel.getTotalValue() }
                        else -> it.sortedBy { itemModel -> itemModel.id }
                    }
                }
            }
            SortOptions.TITLE -> {
                _list.value?.let {
                    when (_sortTitle.value) {
                        true -> it.sortedBy { itemModel -> itemModel.title }
                        false -> it.sortedByDescending { itemModel -> itemModel.title }
                        else -> it.sortedBy { itemModel -> itemModel.id }
                    }
                }
            }
            else -> _list.value
        }
    }

    fun setSortIcon(sortBy: SortOptions) {
        val handleSort = { bool: Boolean? ->
            when (bool) {
                true -> false
                false -> null
                else -> true
            }
        }

        var stringMessage: String? = null
        when (sortBy) {
            SortOptions.PRICE -> {
                _sortPrice.value = handleSort(_sortPrice.value)
                _sortTitle.value = null
                stringMessage = _sortPrice.value?.let {
                    "Ordenando preço por ordem ${if (it) "crescente" else "decrescente"}"
                }
            }
            SortOptions.TITLE -> {
                _sortTitle.value = handleSort(_sortTitle.value)
                _sortPrice.value = null
                stringMessage = _sortTitle.value?.let {
                    "Ordenando titulo por ordem ${if (it) "crescente" else "decrescente"}"
                }
            }
        }
        stringMessage?.let { message(it) }
        sort(sortBy)
    }

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

    private fun message(msg: String) {
        msgString.value = msg
        msgString.value = null
    }

    fun message(): LiveData<String?> = msgString

    fun save(item: ItemModel) {
        if (item.id == 0)
            repository.save(item)
        else{
            repository.update(item)
        }.also {
            getAll()
        }
    }

    fun delete(id: Int) {
        repository.delete(id).let {
            _list.value = _list.value?.filter { i -> i.id != id }
            if (it == 1)
                message("Excluído com sucesso!")
            else
                message("Falha ao excluir")
        }
    }
}