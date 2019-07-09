package com.ned.optimaltime.model.db

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ned.optimaltime.model.entity.History
import com.ned.optimaltime.model.entity.Task
import com.ned.optimaltime.model.typeconvertors.DateTypeConvertor

@Database(entities = [Task::class, History::class], version = 1)
@TypeConverters(DateTypeConvertor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun historyDao(): HistoryDao

    companion object {

        private lateinit var instance: com.ned.optimaltime.model.db.AppDatabase

        @Synchronized
        fun getInstance(context: Context): com.ned.optimaltime.model.db.AppDatabase {
            if (!this::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance
        }

        //Every time the database is created for the first time AKA app is opened after installing for first time
        // this will add an irremovable task "Other"
        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance).execute()
            }
        }

        private class PopulateDbAsyncTask constructor(db: AppDatabase) : AsyncTask<Void, Void, Void>() {
            var taskDao: TaskDao = db.taskDao()

            override fun doInBackground(vararg params: Void?): Void? {
                val t = Task("Other", 0, 0, 0)
                taskDao.insert(t)
                return null
            }
        }
    }
}