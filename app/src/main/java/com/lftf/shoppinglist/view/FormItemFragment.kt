package com.lftf.shoppinglist.view

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentFormItemBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.utils.Price.Companion.formatPrice
import com.lftf.shoppinglist.utils.Price.Companion.parsePrice
import com.lftf.shoppinglist.view.watcher.PriceWatcher
import com.lftf.shoppinglist.viewmodel.MainViewModel

class FormItemFragment : BottomSheetDialogFragment(), View.OnClickListener, View.OnKeyListener {

    private var _binding: FragmentFormItemBinding? = null
    private val viewModel: MainViewModel by activityViewModels()
    private val binding get() = _binding!!
    private var lastItem: ItemModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_ShoppingList_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormItemBinding.inflate(inflater, container, false)
        dialog?.let {
            it.setOnShowListener { dialog ->
                val d = dialog as BottomSheetDialog
                d.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()


        setTextChangedListener()

        binding.editTextQuantity.setOnKeyListener(this)

        setObservers()
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

    private fun getTextWatcherPrice() = object : PriceWatcher(binding.editTextPrice) {
        override fun afterTextChanged(s: Editable?) {
            setTotalPrice()
        }
    }

    private fun setClickListeners() {
        binding.buttonSave.setOnClickListener(this)
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
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_save -> performSaveAction()
        }
    }

    private fun performSaveAction() {
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
            }.also {
                viewModel.save(it)
                dismiss()
            }

            binding.textLayoutTitle.isErrorEnabled = false
        } catch (e: Exception) {
            with(binding.textLayoutTitle) {
                error = e.message
                isErrorEnabled = true
            }
        }
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