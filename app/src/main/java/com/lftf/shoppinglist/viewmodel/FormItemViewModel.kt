package com.lftf.shoppinglist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.ItemRepository

class FormItemViewModel(application: Application) : AndroidViewModel(application) {

    private var msgString = MutableLiveData<String>()
    private val repository = ItemRepository.getInstance(application.applicationContext)

    fun message(msg: String) {
        msgString.value = msg
    }

    fun message(): LiveData<String> = msgString

    fun save(item: ItemModel){
        val id = repository.save(item)
        if (id != -1) message("Item adicionado! \n${item}") else message("Falha!")
    }
}