package com.ned.optimaltime.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.ned.optimaltime.db.AppDatabase
import com.ned.optimaltime.db.HistoryDao
import com.ned.optimaltime.vo.DateCountFormat
import com.ned.optimaltime.vo.History

class HistoryRepository(application: Application) {
    private lateinit var historyList: LiveData<List<History>>
    private lateinit var historyDao: HistoryDao

    init {
        val database = AppDatabase.getInstance(application)
        historyDao = database.historyDao()
        historyList = historyDao.getAllHistory()
    }

    fun insert(history: History) {
        InsertHistoryAsync(historyDao).execute(history)
    }

    fun update(history : History){
        UpdateHistoryAsync(historyDao).execute(history)
    }

    fun delete(history: History){
        DeleteHistoryAsync(historyDao).execute(history)
    }

    fun deleteAllHistory(){
        DeleteAllHistoryAsync(historyDao).execute()
    }

    fun getWeeklyHistory(): List<DateCountFormat> {
        return GetWeeklyHistoryAsync(historyDao).execute().get()
    }

    fun getMonthlyHistory(): List<DateCountFormat> {
        return GetMonthlyHistoryAsync(historyDao).execute().get()
    }

    fun getDailyHistory(): List<DateCountFormat> {
        return GetDailyHistoryAsync(historyDao).execute().get()
    }

    fun getAllHistory(): LiveData<List<History>>{
        return historyList
    }

    fun getWeeklyHistoryById(uid : Long): List<DateCountFormat> {
        return GetWeeklyHistoryByIdAsync(historyDao).execute(uid).get()
    }

    fun getMonthlyHistoryById(uid : Long): List<DateCountFormat> {
        return GetMonthlyHistoryByIdAsync(historyDao).execute(uid).get()
    }

    fun getDailyHistoryById(uid : Long): List<DateCountFormat> {
        return GetDailyHistoryByIdAsync(historyDao).execute(uid).get()
    }
    private class InsertHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<History, Void, Void>() {
        override fun doInBackground(vararg params: History): Void? {
            historyDao.insert(params[0])
            return null
        }
    }

    private class UpdateHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<History, Void, Void>() {
        override fun doInBackground(vararg params: History): Void? {
            historyDao.update(params[0])
            return null
        }
    }

    private class DeleteHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<History, Void, Void>() {
        override fun doInBackground(vararg params: History): Void? {
            historyDao.delete(params[0])
            return null
        }
    }

    private class DeleteAllHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            historyDao.deleteAllHistory()
            return null
        }
    }

    private class GetWeeklyHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Void,Void,List<DateCountFormat>>(){
        override fun doInBackground(vararg params: Void?): List<DateCountFormat> {
            return historyDao.getHistoryWeekly()
        }
    }

    private class GetMonthlyHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Void,Void,List<DateCountFormat>>(){
        override fun doInBackground(vararg params: Void?): List<DateCountFormat> {
            return historyDao.getHistoryMonthly()
        }
    }

    private class GetDailyHistoryAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Void,Void,List<DateCountFormat>>(){
        override fun doInBackground(vararg params: Void?): List<DateCountFormat> {
            return historyDao.getHistoryDaily()
        }
    }

    private class GetWeeklyHistoryByIdAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Long,Void,List<DateCountFormat>>(){
        override fun doInBackground(vararg params: Long?): List<DateCountFormat> {
            return historyDao.getHistoryWeeklyById(params[0]!!)
        }
    }

    private class GetMonthlyHistoryByIdAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Long,Void,List<DateCountFormat>>(){
        override fun doInBackground(vararg params: Long?): List<DateCountFormat> {
            return historyDao.getHistoryMonthlyById(params[0]!!)
        }
    }

    private class GetDailyHistoryByIdAsync constructor(private val historyDao: HistoryDao) : AsyncTask<Long,Void,List<DateCountFormat>>(){
        override fun doInBackground(vararg params: Long?): List<DateCountFormat> {
            return historyDao.getHistoryDailyById(params[0]!!)
        }
    }
}