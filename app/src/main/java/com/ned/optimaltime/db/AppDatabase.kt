package com.ned.optimaltime.db

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ned.optimaltime.vo.History
import com.ned.optimaltime.vo.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(entities = [Task::class, History::class], version = 7)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun historyDao(): HistoryDao

    companion object {

        private lateinit var instance: com.ned.optimaltime.db.AppDatabase

        @Synchronized
        fun getInstance(context: Context): com.ned.optimaltime.db.AppDatabase {
            if (!this::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()      //REMOVE THIS
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
            var histDao : HistoryDao = db.historyDao()

            override fun doInBackground(vararg params: Void?): Void? {
                val t1 = Task("Other", 0, 0, 0)
                val t2 = Task("Study", 0, 0, 0)
                val t3 = Task("Walk", 99, 0, 0)

                taskDao.insert(t1)
                taskDao.insert(t2)
                taskDao.insert(t3)

                val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")

                val h0 = History(LocalDate.parse("2018-07-15", formatter).atStartOfDay(), 1, 25)
                val h1 = History(LocalDate.parse("2019-06-25", formatter).atStartOfDay(), 1, 25)
                val h2 = History(LocalDate.parse("2019-07-10").atStartOfDay(), 2, 25)
                val h3 = History(LocalDate.parse("2019-07-15").atStartOfDay(), 1, 25)
                val h4 = History(LocalDateTime.now(), 3, 25)
                val h5 = History(LocalDate.parse("2019-07-10").atStartOfDay(), 2, 25)
                val h6 = History(LocalDate.parse("2019-01-25").atStartOfDay(), 2, 25)

                histDao.insert(h0)
                histDao.insert(h1)
                histDao.insert(h2)
                histDao.insert(h3)
                histDao.insert(h4)
                histDao.insert(h5)
                histDao.insert(h6)
                return null
            }
        }
    }
}