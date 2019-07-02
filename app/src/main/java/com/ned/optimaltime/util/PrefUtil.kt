package com.ned.optimaltime.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.ned.optimaltime.TimerFragment

class PrefUtil {
    companion object {

        private const val taskDone_ID = "com.ned.optimaltime.taskDone"

        fun getTaskDoneCount(context: Context): Int {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getInt(taskDone_ID, 0)
        }

        fun setTaskDoneCount(count: Int, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(taskDone_ID, count)
            editor.apply()
        }

        private const val taskSkipped_ID = "com.ned.optimaltime.taskSkipped"

        fun getTaskSkippedCount(context: Context): Int {
            val preference = PreferenceManager.getDefaultSharedPreferences(context)
            return preference.getInt(taskSkipped_ID, 0)
        }

        fun setTaskSkippedCount(count: Int, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(taskSkipped_ID, count)
            editor.apply()
        }

        private const val currentTask_ID = "com.ned.optimaltime.task_running"

        fun getCurrentRunningTask(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(currentTask_ID, "")
        }

        fun setCurrentRunningTask(taskToRun: String, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(currentTask_ID, taskToRun)
            editor.apply()
        }

        private const val task_List_ID = "com.ned.optimaltime.task_list"

        fun getTask_List(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            val tasks_List = preferences.getString(task_List_ID, "")
            return tasks_List
        }

        fun setTaskList(task_list_json: String, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(task_List_ID, task_list_json)
            editor.apply()
        }

        fun getTimerLength(context: Context): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            var worklengthStr = ""

            when (PrefUtil.getTimerMode(context!!)) {
                TimerFragment.TimerMode.POMODORO ->
                    worklengthStr = preferences.getString("pref_work_duration", "25")

                TimerFragment.TimerMode.SHORT_BREAK ->
                    worklengthStr = preferences.getString("pref_sbreak_length", "5")

                TimerFragment.TimerMode.LONG_BREAK ->
                    worklengthStr = preferences.getString("pref_lbreak_length", "10")

                else ->
                    worklengthStr = "25"
            }
//            Log.i("Time set",worklengthStr)

            val worklength = worklengthStr.get(0) + "" +
                    if(!worklengthStr.get(1).equals(' '))
                        worklengthStr.get(1)
                    else
                        ""

            return worklength.toInt()
        }


        fun getCountBeforeLongBreak(context : Context): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val countstr = preferences.getString("pref_work_before_lbreak","4")
            val count = countstr.get(0) +
                    if(countstr.get(1).equals(' ')) {
                        ""
                    } else {
                        ""+countstr.get(1)
                    }
            return count.toInt()
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.ned.optimaltime.timer.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.ned.optimaltime.timer.timer_state"

        fun getTimerState(context: Context): TimerFragment.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TimerFragment.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TimerFragment.TimerState, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val TIMER_MODE_ID = "com.ned.optimaltime.timer.timer_mode"

        fun getTimerMode(context: Context): TimerFragment.TimerMode {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_MODE_ID, 0)
            return TimerFragment.TimerMode.values()[ordinal]
        }

        fun setTimerMode(mode: TimerFragment.TimerMode, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = mode.ordinal
            editor.putInt(TIMER_MODE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "com.ned.optimaltime.timer.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }


        private const val ALARM_SET_TIME_ID = "com.ned.optimal.timer.backgrounded_time"

        fun getAlarmSetTime(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }


//        private const val DATE_COUNT_MAP = "com.ned.optimal.timer.date_with_count"
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