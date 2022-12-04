package com.lftf.shoppinglist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentListBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.repository.local.ItemRepository
import com.lftf.shoppinglist.repository.local.MoneyRepository
import com.lftf.shoppinglist.view.adapter.ItemAdapter
import com.lftf.shoppinglist.view.listener.ItemListener
import com.lftf.shoppinglist.view.touchhelper.RecyclerTouchHelper
import com.lftf.shoppinglist.viewmodel.MainViewModel

class ListFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentListBinding? = null
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.Factory(
            ItemRepository(requireContext()),
            MoneyRepository(requireContext())
        )
    }
    private lateinit var adapter: ItemAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        adapter = ItemAdapter(requireContext().applicationContext)
        return binding.root
    }

    private fun setItemListener(): ItemListener = object : ItemListener {
        override fun onClick(item: ItemModel) {
            findNavController().navigate(R.id.action_ListFragment_to_FormItemFragment)
            viewModel.updateLastItem(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener(this)

        configureRecyclerView()

        setOnClickListeners()

        setObservers()
    }

    private fun configureRecyclerView() {

        //Configure callback for swipe right action
        ItemTouchHelper(setItemTouchHelperCallback()).also {
            it.attachToRecyclerView(binding.recyclerList)
        }

        adapter.attachListener(setItemListener())
        binding.recyclerList.layoutManager = LinearLayoutManager(context)
        binding.recyclerList.adapter = adapter
    }

    private fun setOnClickListeners() {
        with(binding.header) {
            arrayOf(
                sortPriceImg, relativePrice, headerPrice,
                headerTitle, relativeTitle, sortTitleImg
            ).forEach { i ->
                i.setOnClickListener(
                    this@ListFragment
                )
            }
        }
    }

    private fun setItemTouchHelperCallback() = object : RecyclerTouchHelper(requireContext()) {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position: Int = viewHolder.adapterPosition

            if (direction == ItemTouchHelper.END) {
                val deletedItem: ItemModel? = viewModel.list.value?.get(position)
                viewModel.deleteAtPosition(position)
                adapter.notifyItemRemoved(position)

                val stringSnack =
                    "Item ${deletedItem?.title} (${deletedItem?.getTotalValue()}) deletado!"
                Snackbar.make(binding.recyclerList, stringSnack, Snackbar.LENGTH_LONG)
                    .setAction("Desfazer") {
                        deletedItem?.let { item -> viewModel.save(item) }
                        adapter.notifyItemInserted(position)
                    }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateLastItem(null)
    }

    private fun setObservers() {
        val setImage = { bool: Boolean? ->
            when (bool) {
                true -> R.drawable.ic_arrow_up
                false -> R.drawable.ic_arrow_down
                else -> R.drawable.ic_remove
            }
        }
        viewModel.sortPrice.observe(viewLifecycleOwner) {
            binding.header.sortPriceImg.setImageResource(setImage(it))
        }

        viewModel.sortTitle.observe(viewLifecycleOwner) {
            binding.header.sortTitleImg.setImageResource(setImage(it))
        }

        viewModel.list.observe(viewLifecycleOwner) { adapter.updateList(it) }

        viewModel.message.observe(viewLifecycleOwner) {
            it?.let { string ->
                Snackbar.make(
                    requireView().context,
                    binding.root,
                    string,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> findNavController().navigate(R.id.action_ListFragment_to_FormItemFragment)
            R.id.relative_price, R.id.sort_price_img, R.id.header_price -> viewModel.changeSortPrice()
            R.id.relative_title, R.id.sort_title_img, R.id.header_title -> viewModel.changeSortTitle()
        }
    }
}