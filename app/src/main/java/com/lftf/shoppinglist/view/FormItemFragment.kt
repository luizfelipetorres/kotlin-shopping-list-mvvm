package com.lftf.shoppinglist.view

import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Half.toFloat
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentFormItemBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class FormItemFragment : Fragment(), View.OnClickListener, View.OnKeyListener {

    private var _binding: FragmentFormItemBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private val binding get() = _binding!!
    private var lastItem: ItemModel? = null
    private val regexPrice: Regex = """[\sR,$.]""".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFormItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()

        setTextChangedListener()

        binding.editTextQuantity.setOnKeyListener(this)

        setObservers()
    }

    private fun getTextWatcherPrice() = object : TextWatcher {
        private var current = ""
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (s.toString() != current) {
                binding.editTextPrice.removeTextChangedListener(this)

                val parsed = parsePrice(s?.toString() ?: "")
                val formatted = formatPrice(parsed)

                current = formatted
                binding.editTextPrice.setText(formatted)
                binding.editTextPrice.setSelection(formatted.length)
                binding.editTextPrice.addTextChangedListener(this)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            setTotalPrice()
        }
    }

    private fun getTextWatcherQuantity() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            setTotalPrice()
        }

    }

    private fun parsePrice(stringPrice: String): Float {
        val stringPrice = stringPrice
        val stringParsed = stringPrice.replace(regexPrice, "")
        return stringParsed.let{ if (it == "") 0f else it.toFloat() / 100 }
    }

    private fun formatPrice(floatPrice: Float): String {
        val lBrazil = Locale("pt", "BR")
        return NumberFormat.getCurrencyInstance(lBrazil).format((floatPrice))
    }

    private fun setTotalPrice() {
        with(binding) {
            val price = parsePrice(binding.editTextPrice.text.toString())

            val quantity = editTextQuantity.text.toString().let { q ->
                if (q.isEmpty()) 1 else q.toInt()
            }

            val total = price * quantity
            if (total > 0) {
                getString(R.string.text_view_total_price).format(total).let { str ->
                    textViewTotalPrice.text = str
                }
            } else {
                textViewTotalPrice.text = ""
            }
        }
    }

    private fun setTextChangedListener() {
        with(binding) {
            editTextPrice.addTextChangedListener(getTextWatcherPrice())
            editTextQuantity.addTextChangedListener(getTextWatcherQuantity())
        }
    }

    private fun setClickListeners() {
        arrayOf(
            binding.buttonSave,
            binding.buttonCancel
        ).forEach { i -> i.setOnClickListener(this) }
    }

    private fun setObservers() {
        viewModel.lastItem.observe(viewLifecycleOwner) {
            with(binding) {
                if (it != null) {
                    lastItem = it
                    editTextTitle.setText(it.title)
                    editTextPrice.setText(formatPrice(it.price))
                    editTextQuantity.setText(
                        it.quantity.toString().let { if (it == "1") "" else it })
                    buttonSave.text = getString(R.string.update)
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_save -> performSaveAction(v)

            R.id.button_cancel -> findNavController().navigate(R.id.action_FormItemFragment_to_ListFragment)
        }
    }

    private fun performSaveAction(v: View) {
        try {
            val title = binding.editTextTitle.text.toString().let {
                if (it == "") throw Exception("Preencha o nome do item!") else it
            }
            val quantity = binding.editTextQuantity.text.toString().let {
                if (it == "") 1 else it.toInt()
            }
            val price = parsePrice(binding.editTextPrice.text.toString())

            ItemModel().apply {
                this.id = lastItem?.id ?: 0
                this.title = title
                this.quantity = quantity
                this.price = price
            }.also { viewModel.save(it) }

            buildSucessAlert(v)
            binding.textLayoutTitle.isErrorEnabled = false
        } catch (e: Exception) {
            with(binding.textLayoutTitle) {
                error = e.message
                isErrorEnabled = true
            }
        }
    }

    private fun buildSucessAlert(v: View) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.alert_title_item_saved))
            .setMessage(getString(R.string.alert_msg_item_saved))
            .setPositiveButton(getString(R.string.alert_positive_button_item_saved)) { _, _ ->
                clearFields()
                binding.buttonSave.text = context?.getString(R.string.save)
                binding.editTextTitle.requestFocus()
            }
            .setNegativeButton(getString(R.string.alert_negative_button_item_saved)) { _, _ ->
                findNavController().navigate(R.id.action_FormItemFragment_to_ListFragment)
                manageKeyboard(v, false)
            }
            .show()
    }

    private fun clearFields() {
        listOf<EditText>(
            binding.editTextTitle,
            binding.editTextPrice,
            binding.editTextQuantity
        ).forEach { i -> i.setText("") }
        viewModel.updateLastItem(null)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}