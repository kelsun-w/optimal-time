package com.ned.optimaltime

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ned.optimaltime.model.Task
import com.ned.optimaltime.util.PrefUtil
import com.ned.optimaltime.util.SwipeUtil



class TaskListFragment : Fragment(), SwipeUtil.SwipeUtilTouchListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainRecyclerAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var data: ArrayList<Task>
    private lateinit var addBtn: Button

    private lateinit var swipeController: SwipeUtil
    private val gson: Gson = Gson()    //for converting json

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

        val contextView: View = getView()!!

        recyclerView = contextView.findViewById<RecyclerView>(R.id.recyclerview)
        linearLayoutManager = LinearLayoutManager(this.activity)

        setupList()

        //adding swiping feature for deleting and editing
        val swipeController = object : SwipeUtil(context!!, this){}
        val itemTouchHelper = ItemTouchHelper(swipeController)

        itemTouchHelper.attachToRecyclerView(recyclerView)

        val addNewTask = contextView.findViewById<EditText>(R.id.addNewTaskName)

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
//        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    //swipe feature
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.START) {
            //backup of removed task for undo purpose
            val deletedTask: Task = data[viewHolder.adapterPosition]
            val deletedIndex = viewHolder.adapterPosition

            //delete the task
            deleteTask(viewHolder.adapterPosition)

            //Create snackbar for undo option
            val snackbar: Snackbar = Snackbar.make(view!!,deletedTask.name+" has been removed!", Snackbar.LENGTH_SHORT)
            snackbar.setAction("UNDO") {
                // undo is selected, restore the deleted item
                adapter.restoreItem(deletedTask, deletedIndex)
            }
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()

        } else if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.END) {
            //edit
        }
    }

    private fun restoreScrollPositionAfterAdded() {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (layoutManager != null) {
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (firstVisibleItemPosition == 0) {
                layoutManager.scrollToPosition(0)
            }
        }
    }

    private fun setupList() {
        data = ArrayList<Task>()

        val json_tasklist = PrefUtil.getTask_List(context!!)

        //First time user is opening app OR user erased previous app data, create an OTHER task
        if (json_tasklist.isEmpty()) {
            data.add(Task("Other"))
            PrefUtil.setTaskList(gson.toJson(data), context!!)
        } else {
            val type = object : TypeToken<ArrayList<Task>>() {}.type
            data = gson.fromJson<ArrayList<Task>>(json_tasklist, type)
        }

        recyclerView.layoutManager = linearLayoutManager
        adapter = MainRecyclerAdapter(data)
        recyclerView.adapter = adapter
    }

    private fun addTask() {
        val text = view?.findViewById<EditText>(R.id.addNewTaskName)?.text.toString()
        if (!text.isEmpty()) {  //user did not input an empty string
            data.add(0, Task(text))
            adapter.notifyItemInserted(0)

            //persist current task list to SharedPreferences
            val currentDataList = gson.toJson(data)
            PrefUtil.setTaskList(currentDataList, context!!)
        }
    }

    private fun deleteTask(position: Int) {
        adapter.removeItem(position)

        //change the current running task from the deleted task to Other
        for (x in data) {
            if (x.name == "Other")
                PrefUtil.setCurrentRunningTask(gson.toJson(x), context!!)
        }
        //update the tasklist
        PrefUtil.setTaskList(gson.toJson(data), context!!)
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }
}