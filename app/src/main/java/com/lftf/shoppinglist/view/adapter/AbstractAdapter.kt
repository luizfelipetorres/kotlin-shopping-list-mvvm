package com.lftf.shoppinglist.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.lftf.shoppinglist.model.AbstractModel

abstract class AbstractAdapter<VH : RecyclerView.ViewHolder, T : AbstractModel>() :
    RecyclerView.Adapter<VH>() {

    protected var list: List<T> = listOf()

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<T>) {
        list = newList
        notifyDataSetChanged()
    }
}