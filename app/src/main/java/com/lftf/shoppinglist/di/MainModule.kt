package com.lftf.shoppinglist.di

import com.lftf.shoppinglist.repository.local.ItemRepository
import com.lftf.shoppinglist.repository.local.MoneyRepository
import com.lftf.shoppinglist.repository.shared.SharedPreferences
import com.lftf.shoppinglist.viewmodel.MainViewModel
import com.lftf.shoppinglist.viewmodel.MoneyViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single {
        ItemRepository(androidApplication().applicationContext)
    }
    single {
        MoneyRepository(androidApplication().applicationContext)
    }

    single {
        SharedPreferences(androidApplication().applicationContext)
    }

    viewModel {
        MainViewModel(
            moneyRepository = get(),
            itemRepository = get(),
            sharedPreferences = get()
        )
    }

    viewModel {
        MoneyViewModel(
            repository = get()
        )
    }
}