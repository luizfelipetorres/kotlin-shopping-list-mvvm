package com.lftf.shoppinglist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lftf.shoppinglist.databinding.FragmentMoneyBinding
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.local.MoneyRepository
import com.lftf.shoppinglist.view.adapter.MoneyAdapter
import com.lftf.shoppinglist.view.touchhelper.RecyclerTouchHelper
import com.lftf.shoppinglist.viewmodel.MoneyViewModel

class MoneyFragment : Fragment(), View.OnClickListener {
    private lateinit var navController: NavController
    private lateinit var binding: FragmentMoneyBinding
    private lateinit var adapter: MoneyAdapter
    private lateinit var viewModel: MoneyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoneyBinding.inflate(LayoutInflater.from(requireContext()))
        adapter = MoneyAdapter(requireContext().applicationContext)
        viewModel = MoneyViewModel.Companion.Factory(
            MoneyRepository(requireContext())
        ).create(MoneyViewModel::class.java).also {
            it.getAll()
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
            adapter.updateList(it)
        }
    }

    private fun setClickListeners() {
        binding.fabAddMoney.setOnClickListener(this)
    }

    private fun setRecyclerView() {
        val list = listOf(
            MoneyModel().apply {
                method = "Alelo Felipe"
                limit = 750f
            },
            MoneyModel().apply {
                method = "Alelo Priscila"
                limit = 200f
            }
        )

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


        adapter.updateList(list)
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

    override fun onDestroy() {
        super.onDestroy()
//        TODO("Implementar método para salvar ao sair")
        viewModel.saveAll()
    }
}