package com.lftf.shoppinglist.view

import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentFormItemBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.viewmodel.MainViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FormItemFragment : Fragment(), View.OnClickListener, View.OnKeyListener {

    private var _binding: FragmentFormItemBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFormItemBinding.inflate(inflater, container, false)
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
    }

    private fun setObservers() {
        viewModel.message().observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_save -> {
                try {
                    val title = binding.editTextTitle.text.toString().let {
                        if (it == "") throw Exception("Preencha o nome do item!") else it
                    }
                    val quantity = binding.editTextQuantity.text.toString().let {
                        if (it == "") 1 else it.toInt()
                    }
                    val price = binding.editTextPrice.text.toString().let {
                        if (it == "") 0f else it.toFloat()
                    }

                    val item = ItemModel(title = title, quantity = quantity, price = price)

                    viewModel.save(item)
                    manageKeyboard(v, show = false)
                    buildSucessAlert(v)
                    binding.textLayoutTitle.isErrorEnabled = false
                }catch (e: Exception){
                    with(binding.textLayoutTitle){
                        error = e.message
                        isErrorEnabled = true
                    }
                }
            }
            R.id.button_cancel -> findNavController().navigate(R.id.action_FormItemFragment_to_ListFragment)

        }
    }

    private fun buildSucessAlert(v: View) {
        AlertDialog.Builder(context)
            .setTitle("Item cadastrado!")
            .setMessage("Deseja cadastrar mais um item OU voltar para a lista?")
            .setPositiveButton("Mais um") { _, _ ->
                clearFields()
                binding.editTextTitle.requestFocus()
                manageKeyboard(v, true)
            }
            .setNegativeButton("Voltar") { _, _ ->
                findNavController().navigate(R.id.action_FormItemFragment_to_ListFragment)
            }
            .show()
    }

    private fun clearFields() {
        listOf<EditText>(
            binding.editTextTitle,
            binding.editTextPrice,
            binding.editTextQuantity
        ).forEach { i -> i.setText("") }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean = when (keyCode) {
        KeyEvent.KEYCODE_ENTER -> {
            manageKeyboard(v, show = false)
            true
        }
        else -> false
    }

    private fun manageKeyboard(v: View, show: Boolean) {
        val inputMethodManager =
            activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (show)
            inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
        else
            inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }
}