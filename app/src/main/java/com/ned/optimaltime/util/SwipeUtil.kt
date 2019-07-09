package com.ned.optimaltime.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeUtil(val context: Context,val listener : SwipeUtilTouchListener) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    public interface SwipeUtilTouchListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    }

    private val deleteIcon = ContextCompat.getDrawable(context, com.ned.optimaltime.R.drawable.ic_delete_white_24dp)!!
    private val editIcon = ContextCompat.getDrawable(context, com.ned.optimaltime.R.drawable.ic_edit_white_24dp)!!
    private val intrinsicWidth = deleteIcon.intrinsicWidth
    private val intrinsicHeight = deleteIcon.intrinsicHeight
    private val background = ColorDrawable()
    private val deletebackgroundColor = Color.parseColor("#f44336")
    private val editbackgroundColor = Color.parseColor("#36f48e")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder,direction,viewHolder.adapterPosition)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        /**
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */

        //Make special task unmovable
        val id : Long = recyclerView.adapter!!.getItemId(viewHolder.adapterPosition)
        if (id == TimerUtil.getSpecialId()) return 0

        return super.getMovementFlags(recyclerView, viewHolder)
    }

    // Draw the background under the view depending on whether user swipes right to edit or left to delete
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        Log.i("translate", dX.toString())
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            if (dX < 0) {
                clearCanvas(
                    c,
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
            } else if (dX > 0) {
                clearCanvas(
                    c,
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    itemView.left + dX,
                    itemView.bottom.toFloat()
                )
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }


        if (dX < 0) {
            // swiping to left to delete
            // put the red delete background
            background.color = deletebackgroundColor
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        } else if (dX > 0) {
            // swiping to right to edit
            // put the green edit background
            background.color = editbackgroundColor
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        }

        background.draw(c)

        // Calculate position of icon
        // The icon and it's positions depend upon the swipe direction
        if (dX < 0) {
            // Swiping LEFT
            // Draw the delete icon
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon.draw(c)

        } else if (dX > 0) {
            // Swiping RIGHT
            // Draw the edit icon
            val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val editIconMargin = (itemHeight - intrinsicHeight) / 2
            val editIconLeft = itemView.left + editIconMargin
            val editIconRight = itemView.left + editIconMargin + intrinsicWidth
            val editIconBottom = editIconTop + intrinsicHeight

            editIcon.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
            editIcon.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}