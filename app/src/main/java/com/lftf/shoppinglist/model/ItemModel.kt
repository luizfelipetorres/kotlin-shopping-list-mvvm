package com.lftf.shoppinglist.model

data class ItemModel(
    var id: Int = 0,
    var title: String,
    var quantity: Int,
    var price: Float
) {

    fun getTotalValue(): Float = price * quantity

    /**
     * Constantes do banco de dados
     */
    object DatabaseDefinition {
        const val TABLE_NAME = "tb_item"

        object Columns {
            const val ID = "id"
            const val TITLE = "title"
            const val QUANTITY = "quantity"
            const val VALUE = "value"
        }
    }
}