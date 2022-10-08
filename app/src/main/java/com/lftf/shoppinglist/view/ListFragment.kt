package com.lftf.shoppinglist.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import com.lftf.shoppinglist.view.adapter.ItemAdapter
import com.lftf.shoppinglist.view.listener.ItemListener
import com.lftf.shoppinglist.viewmodel.MainViewModel

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

    private fun setItemTouchHelperCallback() = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, ItemTouchHelper.END)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val itemHeight = itemView.height
            val itemWidth = itemView.width
            if (dX == 0f) {
                clearCanvas(
                    c,
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                return super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            val isDelete = dX > 0

            val icon = if (isDelete) R.drawable.ic_delete else R.drawable.ic_share

            val pLeft: Int = if (isDelete) itemView.left else (itemWidth - itemHeight)
            val pTop: Int = itemView.top
            val pRight: Int = if (isDelete) (itemView.left + itemHeight) else itemWidth
            val pBottom: Int = (itemView.top + itemHeight)

            requireContext().getDrawable(icon)?.apply {
                setBounds(pLeft, pTop, pRight, pBottom)
                setTint(if (isDelete) Color.RED else Color.BLUE)
                draw(c)
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
            val paint = Paint().apply { color = Color.WHITE }
            c.drawRect(left, top, right, bottom, paint)
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.7f

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

        viewModel.list.observe(viewLifecycleOwner) {
            if (it.size == adapter.getItensListSize())
                adapter.sortList(it)
            else
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
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab -> findNavController().navigate(R.id.action_ListFragment_to_FormItemFragment)
            R.id.relative_price, R.id.sort_price_img, R.id.header_price -> viewModel.changeSortPrice()
            R.id.relative_title, R.id.sort_title_img, R.id.header_title -> viewModel.changeSortTitle()
        }
    }
}