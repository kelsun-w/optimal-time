package com.ned.optimaltime.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(
    tableName = "history_table",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["task_id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class History(
    @ColumnInfo(name = "date_recorded")
    var date: LocalDateTime,
    @ColumnInfo(name = "task_id")
    var tid: Long,
    @ColumnInfo(name = "timer_length")
    var tlength: Long
) : AppEntity {
    @ColumnInfo(name = "history_id")
    @PrimaryKey(autoGenerate = true)
    override var uid: Long = 0

    /**
     * Return a String created from the LocalDateTime object of this History instance, formatted by the pattern supplied
     *
     * @param pattern The pattern to be created from this String, must comply with the standards of java.time.format.DateTimeFormatter
     */
    fun getString(pattern: String): String {
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            return date.format(formatter)
        } catch (e: IllegalArgumentException) {
            error("Cannot create DateTimeFormatter. Invalid Pattern.")
        }
    }

    override fun isSame(other: AppEntity): Boolean {
        return uid == other.uid
    }

    override fun hasSameContent(other: AppEntity): Boolean {

        if (this::class != other::class) return false

        val other = other as History

        return this.date == other.date &&
                this.tid == other.tid &&
                this.tlength == other.tlength
    }
}