package com.lftf.shoppinglist.view.adapter

import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.RowMoneyBinding
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.utils.Price
import com.lftf.shoppinglist.view.adapter.MoneyAdapter.MoneyViewHolder
import com.lftf.shoppinglist.view.listener.MoneyListener
import com.lftf.shoppinglist.view.watcher.PriceWatcher

class MoneyAdapter(val context: Context) : RecyclerView.Adapter<MoneyViewHolder>() {

    private lateinit var binding: RowMoneyBinding
    private var moneyList: List<MoneyModel> = listOf()

    class MoneyViewHolder(private val binding: RowMoneyBinding) : RecyclerView.ViewHolder(binding.root) {
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
                        listener.changeLimit(Price.parsePrice(it.text.toString()))
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
        Log.d("MoneyAdapter", "onCreateViewHolder")
        binding = RowMoneyBinding.inflate(LayoutInflater.from(parent.context))
        return MoneyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoneyViewHolder, position: Int) {
        holder.bind(moneyList[position], object : MoneyListener {
            override fun changeMethod(newMethod: String) {
                moneyList[holder.adapterPosition].method = newMethod
            }

            override fun changeLimit(newLimite: Float) {
                moneyList[holder.adapterPosition].limit = newLimite
            }
        })
    }

    override fun getItemCount(): Int = moneyList.size

    fun updateList(list: List<MoneyModel>) {
        moneyList = list
        notifyDataSetChanged()
    }
}