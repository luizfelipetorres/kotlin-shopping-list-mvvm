package com.lftf.shoppinglist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentMoneyBinding
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.local.MoneyRepository
import com.lftf.shoppinglist.view.adapter.MoneyAdapter
import com.lftf.shoppinglist.view.touchhelper.RecyclerTouchHelper
import com.lftf.shoppinglist.viewmodel.MainViewModel
import com.lftf.shoppinglist.viewmodel.MoneyViewModel

class MoneyFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentMoneyBinding
    private lateinit var adapter: MoneyAdapter
    private lateinit var viewModel: MoneyViewModel
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoneyBinding.inflate(LayoutInflater.from(requireContext()))
        viewModel = MoneyViewModel.Factory(
            MoneyRepository(requireContext())
        ).create(MoneyViewModel::class.java).also { it.getAll() }

        adapter = MoneyAdapter(requireContext().applicationContext) {
            totalLimit(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecyclerView()
        setObservers()
        setClickListeners()
    }

    private fun setObservers() {
        viewModel.list.observe(viewLifecycleOwner) {
            val sum = it.map { e -> e.limit }.sum()
            adapter.updateList(it)
            totalLimit(sum)
            mainViewModel.updateTotalLimit(sum)
        }
    }

    private fun setClickListeners() {
        binding.fabAddMoney.setOnClickListener(this)
    }

    private fun setRecyclerView() {
        ItemTouchHelper(object : RecyclerTouchHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position: Int = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.END) {
                    val deletedItem: MoneyModel? = viewModel.list.value?.get(position)
                    viewModel.deleteAtPosition(position)
                    adapter.notifyItemRemoved(position)

                    val stringSnack =
                        "Item deletado!"
                    Snackbar.make(binding.recyclerViewMoney, stringSnack, Snackbar.LENGTH_LONG)
                        .setAction("Desfazer") {
                            deletedItem?.let { item -> viewModel.save(item) }
                            adapter.notifyItemInserted(position)
                        }.show()
                }
            }
        }).also {
            it.attachToRecyclerView(binding.recyclerViewMoney)
        }
        binding.recyclerViewMoney.adapter = adapter
        binding.recyclerViewMoney.layoutManager = LinearLayoutManager(
            requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.fabAddMoney.id -> viewModel.addToList()
        }
    }

    private fun totalLimit(total: Float) {
        if (total == 0f) {
            binding.totalLimit.visibility = View.GONE
        } else {
            binding.totalLimit.text = getString(R.string.text_view_total_price).format(total)
            mainViewModel.updateTotalLimit(total)
            binding.totalLimit.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveAll()
    }
}