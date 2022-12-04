package com.lftf.shoppinglist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.model.TotalValues
import com.lftf.shoppinglist.repository.local.ItemRepository
import com.lftf.shoppinglist.repository.local.MoneyRepository

class MainViewModel(
    private val itemRepository: ItemRepository,
    private val moneyRepository: MoneyRepository
) : ViewModel() {

    object SaveOptions {
        const val SAVED = 1
        const val UPDATED = 0
    }

    class Factory(
        private val itemRepository: ItemRepository,
        private val moneyRepository: MoneyRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(
                itemRepository = itemRepository,
                moneyRepository = moneyRepository
            ) as T
        }
    }


    private val _list = MutableLiveData<List<ItemModel>>()
    val list: LiveData<List<ItemModel>> = _list

    private var _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _totalValues = MutableLiveData<TotalValues>().apply {
        value = TotalValues(
            totalAmount = getTotalAmount(),
            totalLimit = getTotalLimit()
        )
    }
    val totalValues: LiveData<TotalValues> = _totalValues

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

    private fun getTotalAmount(): Float = _list.value?.toMutableList()?.let {
        if (it.isEmpty())
            0f
        else
            it.map { i -> i.getTotalValue() }.sum()
    } ?: 0f

    fun getAll() {
        val allItens = itemRepository.listAll()
        sort(allItens)
        _totalValues.value = TotalValues(getTotalAmount(), getTotalLimit())
    }

    private fun getTotalLimit(): Float = moneyRepository.listAll().map { it.limit }.sum() ?: 0f

    private fun message(msg: String) {
        _message.value = msg
        _message.value = null
    }

    fun save(item: ItemModel): Int {
        val listFiltered = _list.value?.filter { it.id == item.id }
        val isEmptyList: Boolean = listFiltered?.isEmpty() ?: true
        val returnValue: Int = if (item.id == 0) {
            itemRepository.save(item)
            SaveOptions.SAVED
        } else if (isEmptyList) {
            itemRepository.save(item)
            SaveOptions.SAVED
        } else {
            itemRepository.update(item)
            SaveOptions.UPDATED
        }
        getAll()
        return returnValue
    }

    fun delete(id: Int) {
        itemRepository.delete(id).also {
            message("Excluído com sucesso!")
            getAll()
        }
    }

    fun deleteAtPosition(position: Int) {
        val id = _list.value!![position].id
        delete(id)
    }

    fun updateTotalLimit(newLimit: Float) {
        _totalValues.value = totalValues.value?.let {
            TotalValues(it.totalAmount, newLimit)
        }
    }
}