package com.lftf.shoppinglist.data

import android.content.ClipData
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lftf.shoppinglist.model.ItemModel

/**
 * Classe que será usada na criação de banco de dados NO DISPOSITIVO
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    /**
     * Função executada quando ainda não existe o banco de dados. Faz um CREATE TABLE ...
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SHOPPING_LIST)
    }

    /**
     * Função usada ao alterar a versão do banco de dados
     */
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    /**
     * Objeto com  definição de constantes usada na criação
     */
    companion object {
        private const val DB_NAME = "shopping_list.db"
        private const val DB_VERSION = 1
        private val DB_DEF = ItemModel.DatabaseDefinition
        private val DB_COLUMNS = ItemModel.DatabaseDefinition.Columns

        private const val CREATE_TABLE_SHOPPING_LIST =
            "CREATE TABLE ${DB_DEF.TABLE_NAME} (" +
                    "${DB_COLUMNS.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${DB_COLUMNS.TITLE} TEXT, " +
                    "${DB_COLUMNS.VALUE} REAL, " +
                    "${DB_COLUMNS.QUANTITY} INTEGER)"
    }
}