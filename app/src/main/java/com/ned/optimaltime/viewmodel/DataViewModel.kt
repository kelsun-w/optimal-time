package com.ned.optimaltime.viewmodel

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ned.optimaltime.model.entity.History
import com.ned.optimaltime.model.entity.Task
import com.ned.optimaltime.repositories.HistoryRepository
import com.ned.optimaltime.repositories.TaskRepository

class DataViewModel(@NonNull application: Application) : AndroidViewModel(application) {
    private val taskRepository: TaskRepository = TaskRepository(application)
    private val historyRepository: HistoryRepository = HistoryRepository(application)

    private lateinit var allTask: LiveData<List<Task>>
    private lateinit var allHistory: LiveData<List<History>>

    init {
        allTask = taskRepository.getAllTasks()
        allHistory = historyRepository.getAllHistory()
    }

    fun insert(task: Task) {
        taskRepository.insert(task)
    }

    fun update(task: Task) {
        taskRepository.update(task)
    }

    fun delete(task : Task){
        taskRepository.delete(task)
    }

    fun deleteAll(task: Task){
        taskRepository.deleteAllTasks()
    }

    fun getAllTask() : LiveData<List<Task>>{
        return allTask
    }

    fun insert(history : History) {
        historyRepository.insert(history)
    }

    fun update(history : History) {
        historyRepository.update(history)
    }

    fun delete(history : History){
        historyRepository.delete(history)
    }

    fun deleteAll(history : History){
        historyRepository.deleteAllHistory()
    }

    fun getAllHistory() : LiveData<List<History>>{
        return allHistory
    }
}