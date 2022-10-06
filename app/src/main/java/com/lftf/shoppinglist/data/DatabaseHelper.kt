package com.lftf.shoppinglist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.ItemDAO

@Database(entities = [ItemModel::class], version = 1)
abstract class DatabaseHelper : RoomDatabase() {

    abstract fun itemDao() : ItemDAO

    companion object {
        private lateinit var INSTANCE: DatabaseHelper

        fun getInstance(context: Context): DatabaseHelper {
            if (!::INSTANCE.isInitialized) {
                synchronized(DatabaseHelper::class) {
                    INSTANCE =
                        Room.databaseBuilder(context, DatabaseHelper::class.java, "shopping_list")
                            .addMigrations(MIGRATION_1_2)
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