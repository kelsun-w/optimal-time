package com.ned.optimaltime.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_table"
)
data class Task(
    @ColumnInfo(name = "task_name")
    var name: String,
    @ColumnInfo(name = "done_count")
    var done: Int,
    @ColumnInfo(name = "skipped_count")
    var skipped: Int,
    var priority: Int
) : AppEntity {

    @ColumnInfo(name = "task_id")
    @PrimaryKey(autoGenerate = true)
    override var uid: Long = 0

    override fun isSame(other: AppEntity): Boolean {
        if (this::class != other::class) return false

        return this.uid == other.uid
    }

    override fun hasSameContent(other: AppEntity): Boolean {
        if (this::class != other::class) return false

        val other = other as Task

        return this.name == other.name &&
                this.done == other.done &&
                this.skipped == other.skipped &&
                this.priority == other.priority
    }
}