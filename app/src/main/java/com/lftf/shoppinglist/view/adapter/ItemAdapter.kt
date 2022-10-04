package com.lftf.shoppinglist.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.RowItemBinding
import com.lftf.shoppinglist.model.ItemModel

class ItemAdapter(val context: Context) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    private var itensList: List<ItemModel> = listOf()

    inner class ItemViewHolder(private val binding: RowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ItemModel) {

            val strPrice = if (model.quantity > 1)
                context.getString(R.string.total_price).format(model.price, model.getTotalValue())
            else
                context.getString(R.string.price).format(model.price)

            binding.itemTitle.text = model.title
            binding.itemQuantity.text = model.quantity.toString()
            binding.itemPrice.text = strPrice

            val lightRed = Color.argb(50, 255, 0, 0)
            if (model.price == 0f)
                binding.linearItem.setBackgroundColor(lightRed)

            binding.root.setOnClickListener(View.OnClickListener {
                Snackbar.make(it, "Cliquei em ${model.title}", Snackbar.LENGTH_LONG).show()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itensList[position])
    }

    override fun getItemCount(): Int = itensList.count()

    fun updateList(list: List<ItemModel>) {
        itensList = list
        notifyDataSetChanged()
    }


}