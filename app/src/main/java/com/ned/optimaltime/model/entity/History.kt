package com.ned.optimaltime.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "history_table",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"],
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class History(
    @ColumnInfo(name = "date_recorded")
    var date: Date,
    @ColumnInfo(name = "task_id")
    var tid: Int
) : AppEntity {
    @ColumnInfo(name = "history_id")
    @PrimaryKey(autoGenerate = true)
    override var uid: Long = 0

    override fun isSame(other: AppEntity): Boolean {
        return uid == other.uid
    }

    override fun hasSameContent(other: AppEntity): Boolean {

        if (this::class != other::class) return false

        val other = other as History

        return this.date == other.date &&
                this.tid == other.tid
    }
}