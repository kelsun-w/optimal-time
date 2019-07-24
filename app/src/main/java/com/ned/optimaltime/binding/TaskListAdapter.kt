package com.ned.optimaltime.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ned.optimaltime.R
import com.ned.optimaltime.vo.Task
import kotlinx.android.synthetic.main.task_item_row.view.*

class TaskListAdapter() :
    DiffAdapter<Task, TaskListAdapter.TaskViewHolder>() {

    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_item_row, parent, false)
        return TaskViewHolder(v)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskName.text = getItemAt(position).name
        holder.taskProgress.text = getItemAt(position).done.toString()

        holder.task_start_btn.setOnClickListener {
            listener.onItemClicked(getItemAt(holder.adapterPosition))
        }
    }

    override fun getItemId(position: Int): Long {
        return getItemAt(position).uid
    }

    /**
     * Return an item from the List Adapter's data array at the specified position
     */
    fun getItemAt(position: Int): Task {
        return getItem(position)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task = itemView.findViewById<ViewGroup>(R.id.taskrow) //<<----
        val task_start_btn = task.findViewById<ImageView>(R.id.task_startbutton)
        val taskProgress = task.findViewById<TextView>(R.id.task_progress)
        val taskName: TextView = task.task_name
//        val doneCount = task.task_progress
    }

    interface OnItemClickListener {
        /**
         * Define the action to take when an item in the recycler view is clicked
         */
        fun onItemClicked(t: Task)
    }

    /**
     * Set a listener for receiving calls when an item in the recycler view gets clicked
     */
    fun setOnItemClickedListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}

