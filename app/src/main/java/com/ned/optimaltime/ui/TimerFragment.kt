package com.ned.optimaltime.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ned.optimaltime.R
import com.ned.optimaltime.other.Constants
import com.ned.optimaltime.other.TimerMode
import com.ned.optimaltime.other.TimerState
import com.ned.optimaltime.util.TimerUtil
import com.ned.optimaltime.viewmodel.DataViewModel
import com.ned.optimaltime.vo.History
import com.ned.optimaltime.vo.Task
import kotlinx.android.synthetic.main.timer_fragment.*
import java.time.LocalDateTime
import java.util.*

class TimerFragment : Fragment(), NoticeDialogFragment.NoticeDialogListener {

    private lateinit var viewModel: DataViewModel

    private lateinit var timer: CountDownTimer
    private lateinit var taskRunning: Task

    private var timerLengthSeconds: Long = 0
    private var secondsRemaining: Long = 0

    private lateinit var state: TimerState
    private lateinit var mode: TimerMode

    //Daily Counters
    private var dailyDone = 0
    private var dailySkipped = 0

    //===================================================================================================
    private fun stopTimerDialog() {
        val newFragment = NoticeDialogFragment.getInstance(
            R.string.timerf_stopmsg,
            R.string.timerf_stop_positive,
            R.string.timerf_stop_negative
        )

        fragmentManager?.putFragment(newFragment.arguments!!, NoticeDialogFragment.HOST_FRAGMENT, this)

        newFragment.show(fragmentManager!!, "stop_timer_prompt")
    }

    //Stop Timer Dialog callback
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        //For cases where we are stopping timer when it's paused (The timer object isn't initialized in such state)
        if (::timer.isInitialized)
            timer.cancel()

        onTimerFinished()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        //Do nothing
    }
    //=========================================================================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.timer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(DataViewModel::class.java)

        //title of task selected
        val titleView = view.findViewById<TextView>(R.id.timertask_name)

        if (TimerUtil.getCurrentRunningTask(context!!) != Constants.NULL_TASK_VALUE) {
            val tid = TimerUtil.getCurrentRunningTask(context!!)
            taskRunning = viewModel.getTask(tid)
        } else {
            val tid = TimerUtil.getSpecialId()
            taskRunning = viewModel.getTask(tid)

            TimerUtil.setCurrentRunningTask(tid, context!!)
        }

        titleView.text = context!!.getString(R.string.timerf_task, taskRunning.name)


        fab_debug.setOnClickListener { v ->
            if (::timer.isInitialized)
                debugFinish()   //For saving time while debugging
        }

        fab_start.setOnClickListener { v ->
            startTimer()
            updateButtons()
        }

        fab_pause.setOnClickListener { v ->
            timer.cancel()
            state = TimerState.PAUSED
            updateButtons()
        }

        fab_stop.setOnClickListener { v ->
            if (mode == TimerMode.POMODORO) {
                stopTimerDialog()
            } else {
                if (::timer.isInitialized)
                    timer.cancel()

                onTimerFinished()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        state = TimerUtil.getTimerState(context!!)
        mode = TimerUtil.getTimerMode(context!!)
        dailyDone = TimerUtil.getTaskDoneCount(context!!)
        dailySkipped = TimerUtil.getTaskSkippedCount(context!!)

        Log.d(
            "OnResume",
            "State : ${state.toString()}  Mode : ${mode.toString()} TaskDone : ${TimerUtil.getTaskDoneCount(
                context!!
            ).toString()}"
        )

        initTimer()
        removeAlarm(context!!)
    }

    override fun onPause() {
        super.onPause()

        if (state == TimerState.RUNNING) {
            timer.cancel()
            val wakeUpTime = setAlarm(
                context!!,
                nowSeconds,
                secondsRemaining
            )
        }

        Log.d(
            "OnPause",
            "State : ${state.toString()} Mode : ${mode.toString()} TaskDone : ${TimerUtil.getTaskDoneCount(
                context!!
            ).toString()}"
        )

        //persist the timer data to use on resume of app
        TimerUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, context!!)
        TimerUtil.setSecondsRemaining(secondsRemaining, context!!)
        TimerUtil.setTimerState(state, context!!)
    }

    private fun initTimer() {
        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was running in the background
        if (state == TimerState.STOPPED)
            setNewTimerLength()
        else
            setPreviousTimerLength()


        secondsRemaining = if (state == TimerState.RUNNING || state == TimerState.PAUSED)
            TimerUtil.getSecondsRemaining(context!!)
        else
            timerLengthSeconds

        val alarmSetTime = TimerUtil.getAlarmSetTime(context!!)

        if (alarmSetTime > 0) {
            //Subtract the secondsRemaining from before the alarm was set
            // by the seconds passed during running the background alarm
            secondsRemaining -= nowSeconds - alarmSetTime
        }

        if (secondsRemaining <= 0) {
            //The alarm finished properly
            Toast.makeText(this.context, secondsRemaining.toString(), Toast.LENGTH_SHORT).show()
            onTimerFinished()
        } else if (state == TimerState.RUNNING) {
            //The app was resumed before alarm finished. We have to restart the timer with the remaining seconds
            startTimer()
        }

        if (mode == TimerMode.POMODORO) {
            break_msg.visibility = View.INVISIBLE

        } else {
            break_msg.visibility = View.VISIBLE

        }
        updateButtons()
        updateCountdownUI()

    }

    private fun startTimer() {
        state = TimerState.RUNNING

        TimerUtil.setCurrentTimerLength(timerLengthSeconds, context!!)

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                onTimerFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()       //start the timer
    }

    private fun onTimerFinished() {
        state = TimerState.STOPPED

        if (mode == TimerMode.POMODORO) {
            evaluateTask()
            //if mode is WORK, update to BREAK only if we finished the full work length
            if (secondsRemaining <= 0) {
                updateMode()
            }

        } else {
            //if mode is BREAK, we can just update to WORK
            updateMode()
        }

        if (mode == TimerMode.POMODORO) {
            break_msg.visibility = View.INVISIBLE

        } else {
            break_msg.visibility = View.VISIBLE

        }

        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength()

        progress_countdown.progress = 0

        TimerUtil.setSecondsRemaining(timerLengthSeconds, context!!)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    /**
     *  Makes changes to the data of current task and the overall data of tasks.
     *  If this method was called when the timer expired, then `task done count` increases else `task skip count` increases
     **/
    private fun evaluateTask() {
        if (secondsRemaining <= 0) {
            taskRunning.done++
            dailyDone++

            viewModel.insert(
                History(
                    LocalDateTime.now(),
                    taskRunning.uid,
                    TimerUtil.getCurrentTimerLength(context!!)
                )
            )
            //TODO: increase daily done task count AND check if we reached daily goal AND notify user
        } else {
            taskRunning.skipped++
            dailySkipped++
        }

        // persist the data
        viewModel.update(taskRunning)
        Log.d("onEvaluateTask", "$dailyDone")
        TimerUtil.setTaskDoneCount(dailyDone, context!!)
        TimerUtil.setTaskSkippedCount(dailySkipped, context!!)
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = TimerUtil.getTimerLength(context!!)
        timerLengthSeconds = lengthInMinutes * 60L
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = TimerUtil.getPreviousTimerLengthSeconds(context!!)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinutesUntilFinished = secondsRemaining - (minutesUntilFinished * 60)
        val secondsStr = secondsInMinutesUntilFinished.toString()

        countdown.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateMode() {
        val count = TimerUtil.getTaskDoneCount(context!!)

        if (mode == TimerMode.POMODORO) {
            if (count % TimerUtil.getCountBeforeLongBreak(context!!) == 0)
                mode = TimerMode.LONG_BREAK
            else
                mode = TimerMode.SHORT_BREAK
        } else {
            mode = TimerMode.POMODORO
        }

        TimerUtil.setTimerMode(mode, context!!)
        Log.d("OnUpdateMode", "$mode")
    }

    private fun updateButtons() {
        when (state) {
            TimerState.RUNNING -> {
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }

            TimerState.PAUSED -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }

            TimerState.STOPPED -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
        }
    }

    private fun debugFinish() {
        secondsRemaining = 0
        timer.cancel()
        onTimerFinished()
    }

    //================================================================================================
    companion object {

        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            //what happens when alarms goes off
            val intent = Intent(context, TimerExpiredReciever::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)

            TimerUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReciever::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            TimerUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }
}