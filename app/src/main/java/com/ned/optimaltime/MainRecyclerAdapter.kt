package com.ned.optimaltime

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ned.optimaltime.model.Task
import com.ned.optimaltime.util.PrefUtil
import kotlinx.android.synthetic.main.task_item_row.view.*

class MainRecyclerAdapter(private val dataList: ArrayList<Task>) :
    RecyclerView.Adapter<MainRecyclerAdapter.TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerAdapter.TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_item_row, parent, false)
        return TaskViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MainRecyclerAdapter.TaskViewHolder, position: Int) {
        holder.taskName.text = dataList[position].name
        holder.progress.text = dataList[position].done.toString()

        holder.task_start_btn.setOnClickListener() {
            val gson = Gson()
            val currentTask = gson.toJson(dataList[position])
            val context = holder.task.context

            PrefUtil.setCurrentRunningTask(currentTask,context)
            Log.i("TASK_RUNNING",currentTask)  //debug

            it.findNavController().navigate(R.id.action_task_dest_to_timer_dest)
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task = itemView.findViewById<ViewGroup>(R.id.taskrow)
        val task_start_btn = task.findViewById<ImageView>(R.id.task_startbutton)

        val taskName : TextView = task.task_name
        val progress = task.task_progress
    }
}

