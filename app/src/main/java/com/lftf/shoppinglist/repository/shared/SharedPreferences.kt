package com.lftf.shoppinglist.repository.shared

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferences(val context: Context) {
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    fun saveInt(key: String, value: Int) {
        sharedPreferences.apply {
            edit().putInt(key, value).apply()
        }
        Log.d("SharedPreferences/D", "salvando '$key:$value'")
    }

    fun getInt(key: String) = sharedPreferences.getInt(key, 0)

}