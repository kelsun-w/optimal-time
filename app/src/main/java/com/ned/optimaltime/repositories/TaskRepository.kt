package com.ned.optimaltime.repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.ned.optimaltime.model.db.AppDatabase
import com.ned.optimaltime.model.db.TaskDao
import com.ned.optimaltime.model.entity.Task

class TaskRepository(application: Application) {
    private lateinit var taskList: LiveData<List<Task>>
    private lateinit var taskDao: TaskDao

    init {
        val database = AppDatabase.getInstance(application)
        taskDao = database.taskDao()
        taskList = taskDao.getAllTasks()
    }

    fun insert(task: Task) {
        insertTaskAsync(taskDao).execute(task)
    }

    fun update(task : Task){
        updateTaskAsync(taskDao).execute(task)
    }

    fun delete(task: Task){
        deleteTaskAsync(taskDao).execute(task)
    }

    fun deleteAllTasks(){
        deleteAllTaskAsync(taskDao).execute()
    }

    fun getAllTasks(): LiveData<List<Task>>{
        return taskList
    }

    private class insertTaskAsync constructor(private val taskDao: TaskDao) : AsyncTask<Task, Void, Void>() {
        override fun doInBackground(vararg params: Task): Void? {
            taskDao.insert(params[0])
            return null
        }
    }

    private class updateTaskAsync constructor(private val taskDao: TaskDao) : AsyncTask<Task, Void, Void>() {
        override fun doInBackground(vararg params: Task): Void? {
            taskDao.update(params[0])
            return null
        }
    }

    private class deleteTaskAsync constructor(private val taskDao: TaskDao) : AsyncTask<Task, Void, Void>() {
        override fun doInBackground(vararg params: Task): Void? {
            taskDao.delete(params[0])
            return null
        }
    }

    private class deleteAllTaskAsync constructor(private val taskDao: TaskDao) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            taskDao.deleteAllTasks()
            return null
        }
    }
}