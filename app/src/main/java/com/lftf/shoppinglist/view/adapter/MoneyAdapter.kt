package com.lftf.shoppinglist.view.adapter

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.RowMoneyBinding
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.utils.parsePrice
import com.lftf.shoppinglist.view.adapter.MoneyAdapter.MoneyViewHolder
import com.lftf.shoppinglist.view.listener.MoneyListener
import com.lftf.shoppinglist.view.watcher.PriceWatcher

class MoneyAdapter(val context: Context, val updateSum: (sum: Float) -> Unit) :
    AbstractAdapter<MoneyViewHolder, MoneyModel>() {

    private lateinit var binding: RowMoneyBinding

    class MoneyViewHolder(private val binding: RowMoneyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MoneyModel, listener: MoneyListener) {
            binding.etLimit.let {
                it.addTextChangedListener(object : PriceWatcher(it) {
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        super.onTextChanged(s, start, before, count)
                        listener.changeLimit(it.text.toString().parsePrice())
                    }
                })
                it.setText(it.context.getString(R.string.price).format(model.limit))
            }
            binding.etPaymentMethod.let {
                it.setText(model.method)
                it.addTextChangedListener(object : PriceWatcher(it) {
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        listener.changeMethod(it.text.toString())
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyViewHolder {
        binding = RowMoneyBinding.inflate(LayoutInflater.from(parent.context))
        return MoneyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoneyViewHolder, position: Int) {
        holder.bind(list[position], object : MoneyListener {
            override fun changeMethod(newMethod: String) {
                list[holder.adapterPosition].method = newMethod
            }

            override fun changeLimit(newLimite: Float) {
                list[holder.adapterPosition].limit = newLimite
                updateSum(list.map { e -> e.limit }.sum())
            }
        })
    }
}