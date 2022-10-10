package com.lftf.shoppinglist.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lftf.shoppinglist.databinding.RowMoneyBinding
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.view.adapter.MoneyAdapter.MoneyViewHolder
import com.lftf.shoppinglist.view.watcher.PriceWatcher

class MoneyAdapter(val context: Context) : RecyclerView.Adapter<MoneyViewHolder>() {

    private lateinit var binding: RowMoneyBinding
    private var moneyList: List<MoneyModel> = listOf()

    class MoneyViewHolder(val binding: RowMoneyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MoneyModel) {
//            binding.editTextPaymentLimit.let {
//                it.addTextChangedListener(PriceWatcher(it))
//            }
//            binding.editTextPaymentMethod.setText(model.method)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyViewHolder {
        Log.d("MoneyAdapter", "onCreateViewHolder")
        binding = RowMoneyBinding.inflate(LayoutInflater.from(parent.context))
        return MoneyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoneyViewHolder, position: Int) {
        holder.bind(moneyList[position])
    }

    override fun getItemCount(): Int = moneyList.size

    fun updateList(list: List<MoneyModel>) {
        moneyList = list
        notifyDataSetChanged()
    }

}