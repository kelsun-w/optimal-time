package com.ned.optimaltime.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ned.optimaltime.vo.Task

@Dao
interface TaskDao {
    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("DELETE FROM task_table")
    fun deleteAllTasks()

    @Query("SELECT * FROM task_table WHERE task_id = :id LIMIT 1")
    fun getTask(id : Long): Task
    
    @Query("SELECT * FROM task_table")
    fun getAllTasks() : LiveData<List<Task>>

}