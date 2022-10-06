package com.lftf.shoppinglist.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentListBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.view.adapter.ItemAdapter
import com.lftf.shoppinglist.view.listener.ItemListener
import com.lftf.shoppinglist.viewmodel.MainViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
const val TAG = "ListFragment"

class ListFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentListBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener(this)

        viewModel.updateLastItem(null)

        val listener = object : ItemListener {
            override fun onLongClick(id: Int, callback: () -> Unit): Boolean {
                AlertDialog.Builder(context)
                    .setTitle("Excluir item")
                    .setMessage("Tem certeza que deseja excluir?")
                    .setPositiveButton("Sim") { _, _ ->
                        viewModel.delete(id)
                        callback
                    }
                    .setNegativeButton("Não") { _, _ ->

                    }
                    .show()
                return true
            }

            override fun onClick(item: ItemModel) {
                findNavController().navigate(R.id.action_ListFragment_to_FormItemFragment)
                viewModel.updateLastItem(item)
            }
        }
        adapter.attachListener(listener)
        binding.recyclerList.layoutManager = LinearLayoutManager(context)
        binding.recyclerList.adapter = adapter

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
        setObservers()
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

        viewModel.list.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

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
        Log.d(TAG, "onDestroy")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> findNavController().navigate(R.id.action_ListFragment_to_FormItemFragment)
            R.id.relative_price, R.id.sort_price_img, R.id.header_price -> viewModel.setSortIcon(
                MainViewModel.SortOptions.PRICE
            )
            R.id.relative_title, R.id.sort_title_img, R.id.header_title -> viewModel.setSortIcon(
                MainViewModel.SortOptions.TITLE
            )
        }
    }
}