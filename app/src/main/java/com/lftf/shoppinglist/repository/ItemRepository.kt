package com.lftf.shoppinglist.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.lftf.shoppinglist.data.DatabaseHelper
import com.lftf.shoppinglist.model.ItemModel

class ItemRepository private constructor(context: Context) {
    /**
     * Variáveis auxiliares
     */

    companion object{

        private lateinit var repository: ItemRepository

        fun getInstance(context: Context): ItemRepository{
            if (!::repository.isInitialized){
                repository = ItemRepository(context)
            }
            return repository
        }
    }

    private val dbHelper = DatabaseHelper(context)
    private val DB_DEF = ItemModel.DatabaseDefinition
    private val DB_COLUMNS = ItemModel.DatabaseDefinition.Columns

    fun save(item: ItemModel): Int {
        val db = dbHelper.writableDatabase
        val itemValues = ContentValues()

        with(itemValues) {
            put(DB_COLUMNS.TITLE, item.title)
            put(DB_COLUMNS.QUANTITY, item.quantity)
            put(DB_COLUMNS.VALUE, item.value)
        }

        Log.d("ItemRepository", "item adicionado: $item")
        return db.insert(DB_DEF.TABLE_NAME, null, itemValues).toInt()
    }

    fun getItens(): ArrayList<ItemModel> {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DB_DEF.TABLE_NAME, null, null, null, null, null, null, null
        )

        var itens = ArrayList<ItemModel>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                var item = ItemModel(
                    id = cursor.getInt(cursor.getColumnIndex(DB_COLUMNS.ID).toInt()),
                    title = cursor.getString(cursor.getColumnIndex(DB_COLUMNS.TITLE).toInt()),
                    quantity = cursor.getInt(cursor.getColumnIndex(DB_COLUMNS.QUANTITY).toInt()),
                    value = cursor.getFloat(cursor.getColumnIndex(DB_COLUMNS.VALUE).toInt())
                )
                itens.add(item)
            }
        }
        return itens
    }

    fun getItem(id: Int): ItemModel{
        val db = dbHelper.readableDatabase

        val list = getItens()
        return list.filter { item -> item.id == id }.single()
    }

    fun delete(id: Int): Int {
        val db = dbHelper.writableDatabase

        return db.delete(DB_DEF.TABLE_NAME, "${DB_COLUMNS.ID} = ?", arrayOf(id.toString()))
    }

    /**
     * Retorna 1 se for alterado e 0 se não for
     */
    fun update(item: ItemModel): Int{
        val db = dbHelper.writableDatabase

        val content= ContentValues().apply {
            put(DB_COLUMNS.ID, item.id)
            put(DB_COLUMNS.TITLE, item.title)
            put(DB_COLUMNS.VALUE, item.value)
            put(DB_COLUMNS.QUANTITY, item.quantity)
        }

        val selection = "${DB_COLUMNS.ID} = ?"
        val selectionArgs = arrayOf(item.id.toString())
        return db.update(DB_DEF.TABLE_NAME, content, selection, selectionArgs)
    }
}