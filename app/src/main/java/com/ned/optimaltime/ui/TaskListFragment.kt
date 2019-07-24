package com.ned.optimaltime.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ned.optimaltime.R
import com.ned.optimaltime.binding.TaskListAdapter
import com.ned.optimaltime.util.SwipeUtil
import com.ned.optimaltime.util.TimerUtil
import com.ned.optimaltime.viewmodel.DataViewModel
import com.ned.optimaltime.vo.Task


class TaskListFragment : Fragment(), SwipeUtil.SwipeUtilTouchListener {
    private lateinit var viewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.recyclerview_tasklist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(DataViewModel::class.java)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        adapter = TaskListAdapter()

        setupList()

        adapter.setOnItemClickedListener(object : TaskListAdapter.OnItemClickListener {
            override fun onItemClicked(t: Task) {
                TimerUtil.setCurrentRunningTask(t.uid, context!!)
                findNavController().navigate(R.id.action_task_dest_to_timer_dest)
            }
        })

        //adding swiping feature for deleting and editing
        val swipeController = object : SwipeUtil(context!!, this) {}
        val itemTouchHelper = ItemTouchHelper(swipeController)

        itemTouchHelper.attachToRecyclerView(recyclerView)

        //adding new task feature
        val addNewTask = view.findViewById<EditText>(R.id.addNewTaskName)

        addNewTask.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addTask()
                restoreScrollPositionAfterAdded()
                hideSoftKeyboard(activity as Activity)
                //Clearing input
                addNewTask.setText("")
                true
            } else {
                false
            }
        }
    }

    private fun setupList() {
        val linearLayoutManager = LinearLayoutManager(this.activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        viewModel.getAllTask().observe(this, Observer { updatedList ->
            adapter.submitList(updatedList)
        })
    }

    //swipe feature
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.START) {
            deleteTask(viewHolder.adapterPosition)

        } else if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.END) {
            //edit
        }
    }

    private fun restoreScrollPositionAfterAdded() {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
        if (layoutManager != null) {
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (firstVisibleItemPosition == 0) {
                layoutManager.scrollToPosition(0)
            }
        }
    }

    private fun addTask() {
        val text = view?.findViewById<EditText>(R.id.addNewTaskName)?.text.toString()
        if ((text.trim()).isNotEmpty()) {  //user did not input an empty string
            viewModel.insert(Task(text.trim(), 0, 0, 0))

        }
    }

    private fun deleteTask(position: Int) {
        val dt = adapter.getItemAt(position)

        //Forbid user from deleting the special task
        if (dt.uid == TimerUtil.getSpecialId()) {
            // TODO: ERROR MESSAGE
        } else {
            viewModel.delete(dt)
        }
        //change the current running task from the deleted task to the special task
        TimerUtil.setCurrentRunningTask(TimerUtil.getSpecialId(), context!!)
    }

    private fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }
}