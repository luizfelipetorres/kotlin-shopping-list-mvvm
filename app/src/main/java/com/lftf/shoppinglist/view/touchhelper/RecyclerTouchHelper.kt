package com.lftf.shoppinglist.view.touchhelper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lftf.shoppinglist.R

abstract class RecyclerTouchHelper(val context: Context) : ItemTouchHelper.Callback() {
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

        context.getDrawable(icon)?.apply {
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

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.3f
}
