package com.ned.optimaltime.viewmodel

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ned.optimaltime.repository.HistoryRepository
import com.ned.optimaltime.repository.TaskRepository
import com.ned.optimaltime.vo.DateCountFormat
import com.ned.optimaltime.vo.History
import com.ned.optimaltime.vo.Task

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

    fun delete(task: Task) {
        taskRepository.delete(task)
    }

    fun deleteAll(task: Task) {
        taskRepository.deleteAllTasks()
    }

    fun getTask(id: Long): Task {
        return taskRepository.getTask(id)
    }

    fun getAllTask(): LiveData<List<Task>> {
        return allTask
    }

    fun insert(history: History) {
        historyRepository.insert(history)
    }

    fun update(history: History) {
        historyRepository.update(history)
    }

    fun delete(history: History) {
        historyRepository.delete(history)
    }

    fun deleteAll(history: History) {
        historyRepository.deleteAllHistory()
    }

    fun getWeeklyHistory() : List<DateCountFormat> {
        return historyRepository.getWeeklyHistory()
    }

    fun getMonthlyHistory() : List<DateCountFormat> {
        return historyRepository.getMonthlyHistory()
    }

    fun getDailyHistory() : List<DateCountFormat> {
        return historyRepository.getDailyHistory()
    }

    fun getAllHistory(): LiveData<List<History>> {
        return allHistory
    }

    fun getWeeklyHistoryById(uid : Long) : List<DateCountFormat> {
        return historyRepository.getWeeklyHistoryById(uid)
    }

    fun getMonthlyHistoryById(uid : Long) : List<DateCountFormat> {
        return historyRepository.getMonthlyHistoryById(uid)
    }

    fun getDailyHistoryById(uid : Long) : List<DateCountFormat> {
        return historyRepository.getDailyHistoryById(uid)
    }
}