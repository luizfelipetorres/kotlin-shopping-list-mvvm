package com.lftf.shoppinglist.view

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lftf.shoppinglist.R
import com.lftf.shoppinglist.databinding.FragmentFormItemBinding
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.utils.*
import com.lftf.shoppinglist.view.watcher.PriceWatcher
import com.lftf.shoppinglist.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormItemFragment : BottomSheetDialogFragment(), View.OnClickListener, View.OnKeyListener {

    private var _binding: FragmentFormItemBinding? = null
    private val viewModel: MainViewModel by activityViewModel()
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
            val price = editTextPrice.text.toString().parsePrice()

            val quantity = editTextQuantity.text.toString().let { q ->
                if (q.isEmpty()) 1 else q.toInt()
            }

            val total = price * quantity
            if (total > 0) {
                getString(R.string.text_view_total_price).format(total.formatPrice()).let { str ->
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
        viewModel.lastItem.observe(viewLifecycleOwner) { itemModel ->
            with(binding) {
                lastItem = itemModel
                itemModel?.let {
                    editTextTitle.setText(itemModel.title)
                    editTextPrice.setText(itemModel.price.formatPrice())
                    editTextQuantity.setText(
                        itemModel.quantity.toString().let { quantity ->
                            if (quantity == "1") "" else quantity
                        }
                    )
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
            ItemModel().apply {
                this.id = lastItem?.id ?: 0
                this.title = binding.getTitle()
                this.quantity = binding.getQuantity()
                this.price = binding.getPrice()
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

    private fun clearFields() {
        with(binding) {
            listOf<EditText>(
                editTextTitle,
                editTextPrice,
                editTextQuantity
            ).forEach { i -> i.setText("") }
        }
        viewModel.updateLastItem(null)
    }

    private fun manageKeyboard(v: View, show: Boolean) {
        val inputMethodManager =
            activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (show)
            inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
        else
            inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onDismiss(dialog: DialogInterface) {
        clearFields()
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}