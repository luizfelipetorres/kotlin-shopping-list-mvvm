package com.lftf.shoppinglist.view

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentFormItemBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.viewmodel.FormItemViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FormItemFragment : Fragment(), View.OnClickListener, View.OnKeyListener {

    private var _binding: FragmentFormItemBinding? = null
    private lateinit var viewModel: FormItemViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFormItemBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(FormItemViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrayOf(
            binding.buttonSave,
            binding.buttonCancel
        ).forEach { i -> i.setOnClickListener(this) }

        binding.editTextQuantity.setOnKeyListener(this)
        setObservers()
//        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }
    }

    private fun setObservers() {
        viewModel.message().observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_save -> {
                val item = ItemModel(
                    title = binding.editTextTitle.text.toString(),
                    quantity = binding.editTextQuantity.text.toString()
                        .let { if (it == "") 1 else it.toInt() },
                    value = binding.editTextPrice.text.toString().let {
                        if (it == "") 0f else it.toFloat()
                    } as Float
                ).let { viewModel.save(it) }
                hideKeyboard(v)
            }
            R.id.button_cancel -> {
                viewModel.message("Cliquei em cancelar")
                hideKeyboard(v)
            }
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean = when (keyCode) {
        KeyEvent.KEYCODE_ENTER -> {
            hideKeyboard(v)
            true
        }
        else -> false
    }

    private fun hideKeyboard(v: View) {
        val inputMethodManager =
            activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }
}