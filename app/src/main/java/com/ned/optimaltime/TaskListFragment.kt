package com.ned.optimaltime

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ned.optimaltime.model.Task
import com.ned.optimaltime.util.PrefUtil

class TaskListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MainRecyclerAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var data: ArrayList<Task>
    private lateinit var addBtn: Button


    private lateinit var gson: Gson    //for converting json

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

        gson = Gson()

        setupList()

        val addNewTask = contextView.findViewById<EditText>(R.id.addNewTaskName)

        addNewTask.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.e("New Task Label Check", "Its working")
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

    private fun restoreScrollPositionAfterAdded() {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (layoutManager != null) {
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (firstVisibleItemPosition == 0) {
                layoutManager.scrollToPosition(0)
            }
        }
    }

    private fun addTask() {
        val text = view?.findViewById<EditText>(R.id.addNewTaskName)?.text.toString()
        if (!text.isEmpty()) {  //user did not input an empty string
            data.add(0, Task(text))
            adapter.notifyItemInserted(0)

            //persist current task list to SharedPreferences
            val currentDataList = gson.toJson(data)
            PrefUtil.setTaskList(currentDataList, context!!)
            Log.i("JSON DATA", currentDataList)  //debug

        }
    }

    private fun removeTask() {
        //TODO: Remove task except Other
    }

    private fun setupList() {
        data = ArrayList<Task>()
//        data.add(Task("Study Java"))
//        data.add(Task("Workout Gym"))
//        data.add(Task("Gardening"))
//        data.add(Task("Meeting"))
//        data.add(Task("Project"))
//        data.add(Task("Workout Gym"))
//        data.add(Task("Gardening"))
//        data.add(Task("Meeting"))
//        data.add(Task("Project"))
//        data.add(Task("Gardening"))
//        data.add(Task("Meeting"))
//        data.add(Task("Project"))
//        data.add(Task("Gardening"))
//        data.add(Task("Meeting"))
//        data.add(Task("Project"))
//        data.add(Task("Gardening"))
//        data.add(Task("Meeting"))
//        data.add(Task("Project"))

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

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }
}