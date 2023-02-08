package com.lftf.shoppinglist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.model.TotalValues
import com.lftf.shoppinglist.repository.local.ItemRepository
import com.lftf.shoppinglist.repository.local.MoneyRepository
import com.lftf.shoppinglist.repository.shared.SharedPreferences

class MainViewModel(
    private val itemRepository: ItemRepository,
    private val moneyRepository: MoneyRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    object SaveOptions {
        const val SAVED = 1
        const val UPDATED = 0
    }

    object SortOptions {
        const val NONE = 0
        const val UP = 1
        const val DOWN = 2
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

    private val _sortPrice = MutableLiveData<Int>().apply {
        value = sharedPreferences.getInt("sort_price")
    }
    val sortPrice: LiveData<Int> = _sortPrice

    private val _sortTitle = MutableLiveData<Int>().apply {
        value = sharedPreferences.getInt("sort_title")
    }
    val sortTitle: LiveData<Int> = _sortTitle

    fun updateLastItem(item: ItemModel?) {
        _lastItem.value = item
    }

    private fun sort(list: List<ItemModel>) {
        _list.value = list.let {
            if (_sortPrice.value != SortOptions.NONE) {
                when (_sortPrice.value) {
                    SortOptions.UP -> it.sortedBy { item -> item.getTotalValue() }
                    else -> it.sortedByDescending { item -> item.getTotalValue() }
                }
            } else if (_sortTitle.value != SortOptions.NONE) {
                when (_sortTitle.value) {
                    SortOptions.UP -> it.sortedBy { item -> item.title }
                    else -> it.sortedByDescending { item -> item.title }
                }
            } else {
                it.sortedBy { item -> item.id }
            }
        }
    }

    private fun changeSort(value: Int): Int = when (value) {
        SortOptions.NONE -> SortOptions.UP
        SortOptions.UP -> SortOptions.DOWN
        else -> SortOptions.NONE
    }

    fun changeSortPrice() {
        val priceMessage: (Int) -> String = {
            "Ordenando preço por ordem ${
                when (it) {
                    SortOptions.UP -> "crescente"
                    SortOptions.DOWN -> "decrescente"
                    else -> ""
                }
            }"
        }
        _sortPrice.value = _sortPrice.value?.let {
            changeSort(it).also { res ->
                message(priceMessage(res))
                sharedPreferences.saveInt("sort_price", res)
            }
        }
        SortOptions.NONE.let {
            if (_sortTitle.value != it) {
                _sortTitle.value = it
                sharedPreferences.saveInt("sort_title", it)
            }
        }
        _list.value?.let { sort(it) }
    }

    fun changeSortTitle() {
        val titleMessage: (Int) -> String = {
            "Ordenando titulo por ordem alfabética ${
                if (it == SortOptions.DOWN) "inversa" else ""
            }"
        }

        _sortTitle.value = _sortTitle.value?.let {
            changeSort(it).also { res ->
                message(titleMessage(res))
                sharedPreferences.saveInt("sort_title", res)
            }
        }
        SortOptions.NONE.let {
            if (_sortPrice.value != it) {
                _sortPrice.value = it
                sharedPreferences.saveInt("sort_price", it)
            }
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

    private fun getTotalLimit(): Float = moneyRepository.listAll().map { it.limit }.sum()

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