package com.ned.optimaltime.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.ned.optimaltime.other.Constants
import com.ned.optimaltime.other.TimerMode
import com.ned.optimaltime.other.TimerState

/**
 * Wrapper class for storing values in DefaultSharedPreferences that are related to the Timer Fragment
 */
class TimerUtil {
    companion object {
        private const val CURRENT_TIMERLENGTH = "com.ned.optimaltimeTimer.current_length_seconds"
        private const val CURRENTTASK_ID = "com.ned.optimaltimeTask_running"
        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.ned.optimaltimeTimer.previous_timer_length_seconds"
        private const val SECONDS_REMAINING_ID = "com.ned.optimaltimeTimer.seconds_remaining"
        private const val TASK_DONE_COUNT_ID = "com.ned.optimaltimeTimerTask_done_count"
        private const val TASK_SKIPPED_COUNT_ID = "com.ned.optimaltimeTimerTask_skip_count"
        private const val ALARM_SET_TIME_ID = "com.ned.optimalTimer.backgrounded_time"
        private const val TIMER_STATE_ID = "com.ned.optimaltimeTimerTimer_state"
        private const val TIMER_MODE_ID = "com.ned.optimaltimeTimerTimer_mode"

        /**
         * Returns the id of the irremovable task "Other" in the Database
         */
        fun getSpecialId(): Long {
            return Constants.SPECIAL_TASK_ID
        }

        fun setCurrentTimerLength(seconds : Long, context : Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(CURRENT_TIMERLENGTH, seconds)
            editor.apply()
        }

        fun getCurrentTimerLength(context: Context) : Long{
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getLong(CURRENT_TIMERLENGTH, 0)
        }
        /**
         * Sets the id of the task that will be evaluated when timer finishes.
         */
        fun setCurrentRunningTask(taskID: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(CURRENTTASK_ID, taskID)
            editor.apply()
        }

        /**
         * Returns the id of the task that is currently running the Timer, else returns -1 if no task is set
         */
        fun getCurrentRunningTask(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(CURRENTTASK_ID, Constants.NULL_TASK_VALUE)
        }

        fun getTimerLength(context: Context): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            var worklengthStr = ""

            when (TimerUtil.getTimerMode(context)) {
                TimerMode.POMODORO ->
                    worklengthStr = preferences.getString("pref_work_duration", "25")

                TimerMode.SHORT_BREAK ->
                    worklengthStr = preferences.getString("pref_sbreak_length", "5")

                TimerMode.LONG_BREAK ->
                    worklengthStr = preferences.getString("pref_lbreak_length", "10")

                else ->
                    worklengthStr = "25"
            }

            val worklength = worklengthStr.get(0) + "" +
                    if (worklengthStr.length > 1 && worklengthStr[1] != ' ')
                        worklengthStr[1]
                    else
                        ""

            return worklength.toInt()
        }

        fun getPreviousTimerLengthSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        fun getSecondsRemaining(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

        fun getTaskDoneCount(context: Context): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TASK_DONE_COUNT_ID, 0)
        }

        fun setTaskDoneCount(count: Int, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(TASK_DONE_COUNT_ID, count)
            editor.apply()
        }

        fun getTaskSkippedCount(context: Context): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TASK_SKIPPED_COUNT_ID, 0)
        }

        fun setTaskSkippedCount(count: Int, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(TASK_SKIPPED_COUNT_ID, count)
            editor.apply()
        }

        fun getCountBeforeLongBreak(context: Context): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val countstr = preferences.getString("pref_work_before_lbreak", "4 ")
            val count = countstr.get(0) +
                    if (countstr.get(1).equals(' ')) {
                        ""
                    } else {
                        "" + countstr.get(1)
                    }
            return count.toInt()
        }

        fun getAlarmSetTime(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }


        fun getTimerState(context: Context): TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TimerState.values()[ordinal]
        }

        fun setTimerState(state: TimerState, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        fun getTimerMode(context: Context): TimerMode {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_MODE_ID, 0)
            return TimerMode.values()[ordinal]
        }

        fun setTimerMode(mode: TimerMode, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = mode.ordinal
            editor.putInt(TIMER_MODE_ID, ordinal)
            editor.apply()
        }

//        private coZznst val DATE_COUNT_MAP = "com.ned.optimalTimer.date_with_count"
//
//        fun saveDateWithCount(dataMap : String, context : Context){
//            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
//            editor.putString(DATE_COUNT_MAP, dataMap)
//            editor.apply()
//        }
//
//        fun getDateWithCount(context : Context): String{
//            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//            return preferences.getString(DATE_COUNT_MAP,"")
//        }
    }
}