package com.lftf.shoppinglist.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentListBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.view.adapter.ItemAdapter
import com.lftf.shoppinglist.view.listener.ItemListener
import com.lftf.shoppinglist.viewmodel.MainViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentListBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter
    private val binding get() = _binding!!
    private var sortIcon: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        adapter = ItemAdapter(requireContext().applicationContext)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener(this)

        viewModel.getAll()
        viewModel.updateLastItem(null)

        val listener = object : ItemListener {
            override fun onLongClick(id: Int): Boolean {
                viewModel.delete(id)
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

        binding.header.sortImg.setOnClickListener(this)
        setObservers()
    }

    private fun setObservers() {
        viewModel.listItens.observe(viewLifecycleOwner, Observer {
            adapter.updateList(it)
        })

        viewModel.getSortIcon().observe(viewLifecycleOwner, Observer {
            binding.header.sortImg.setImageResource(
                if (it)
                    R.drawable.ic_arrow_up
                else
                    R.drawable.ic_arrow_down
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> findNavController().navigate(R.id.action_ListFragment_to_FormItemFragment)
            R.id.sort_img -> viewModel.setSortIcon()
        }
    }
}