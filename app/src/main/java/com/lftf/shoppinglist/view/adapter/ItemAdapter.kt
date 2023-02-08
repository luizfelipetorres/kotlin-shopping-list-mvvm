package com.lftf.shoppinglist.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.RowItemBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.utils.formatPrice
import com.lftf.shoppinglist.view.listener.ItemListener

class ItemAdapter(val context: Context) : AbstractAdapter<ItemAdapter.ItemViewHolder, ItemModel>() {
    private lateinit var listener: ItemListener

    inner class ItemViewHolder(
        private val binding: RowItemBinding,
        private val listener: ItemListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ItemModel) {

            val strPrice = if (model.quantity > 1)
                context.getString(R.string.total_price).format(
                    model.price.formatPrice(),
                    model.getTotalValue().formatPrice()
                )
            else
                context.getString(R.string.price).format(model.price.formatPrice())

            binding.itemTitle.text = model.title
            binding.itemQuantity.text = model.quantity.toString()
            binding.itemPrice.text = strPrice

            binding.linearItem.setBackgroundColor(
                if (model.price == 0f || model.quantity == 0)
                    Color.argb(50, 255, 0, 0)
                else
                    Color.WHITE
            )

            binding.root.setOnClickListener {
                listener.onClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun attachListener(listener: ItemListener) {
        this.listener = listener
    }
}