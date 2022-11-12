package com.lftf.shoppinglist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lftf.shoppinglist.databinding.FragmentMoneyBinding
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.view.adapter.MoneyAdapter

class MoneyFragment : Fragment(), View.OnClickListener {
    private lateinit var navController: NavController
    private lateinit var binding: FragmentMoneyBinding
    private lateinit var adapter: MoneyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoneyBinding.inflate(LayoutInflater.from(requireContext()))
        adapter = MoneyAdapter(requireContext().applicationContext)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecyclerView()

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.buttonCancel.setOnClickListener(this);
    }

    private fun setRecyclerView() {
        val list = listOf<MoneyModel>(MoneyModel(), MoneyModel(0, "Alelo", 100f))
        adapter.updateList(list)
        binding.recyclerViewMoney.adapter = adapter
        binding.recyclerViewMoney.layoutManager = LinearLayoutManager(
            requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    override fun onClick(v: View) {
        when(v.id){
            binding.buttonCancel.id -> findNavController().navigateUp()
        }
    }
}