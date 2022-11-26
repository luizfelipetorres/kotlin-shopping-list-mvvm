package com.lftf.shoppinglist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.local.dao.ItemDAO
import com.lftf.shoppinglist.repository.local.dao.MoneyDAO

@Database(entities = [ItemModel::class, MoneyModel::class], version = 2)
abstract class DatabaseHelper : RoomDatabase() {

    abstract fun itemDao() : ItemDAO
    abstract fun moneyDao(): MoneyDAO

    companion object {
        private lateinit var INSTANCE: DatabaseHelper

        fun getInstance(context: Context): DatabaseHelper {
            if (!::INSTANCE.isInitialized) {
                synchronized(DatabaseHelper::class) {
                    INSTANCE =
                        Room.databaseBuilder(context, DatabaseHelper::class.java, "shopping_list")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return INSTANCE
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DELETE FROM tb_item")
            }
        }
    }
}