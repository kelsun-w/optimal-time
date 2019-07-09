package com.ned.optimaltime.repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.ned.optimaltime.model.db.AppDatabase
import com.ned.optimaltime.model.db.HistoryDao
import com.ned.optimaltime.model.entity.History

class HistoryRepository(application: Application) {
    private lateinit var historyList: LiveData<List<History>>
    private lateinit var historyDao: HistoryDao

    init {
        val database = AppDatabase.getInstance(application)
        historyDao = database.historyDao()
        historyList = historyDao.getAllHistory()
    }

    fun insert(history: History) {
        insertHistoryAsync(historyDao).execute(history)
    }

    fun update(history : History){
        updateHistoryAsync(historyDao).execute(history)
    }

    fun delete(history: History){
        deleteHistoryAsync(historyDao).execute(history)
    }

    fun deleteAllHistory(){
        deleteAllHistoryAsync(historyDao).execute()
    }

    fun getAllHistory(): LiveData<List<History>>{
        return historyList
    }

    private class insertHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<History, Void, Void>() {
        override fun doInBackground(vararg params: History): Void? {
            historyDao.insert(params[0])
            return null
        }
    }

    private class updateHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<History, Void, Void>() {
        override fun doInBackground(vararg params: History): Void? {
            historyDao.update(params[0])
            return null
        }
    }

    private class deleteHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<History, Void, Void>() {
        override fun doInBackground(vararg params: History): Void? {
            historyDao.delete(params[0])
            return null
        }
    }

    private class deleteAllHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            historyDao.deleteAllHistory()
            return null
        }
    }
}