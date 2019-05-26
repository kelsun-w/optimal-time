package com.ned.optimaltime

import android.app.Activity
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
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ned.optimaltime.model.Task
import com.ned.optimaltime.util.PrefUtil
import kotlinx.android.synthetic.main.timer_fragment.*
import java.util.*

class TimerFragment : Fragment() {
    //TODO: BUG in OnViewCreated()  when app closes in PAUSED state and reopens, STOP button is clickable calling to Timer's stop() method
        // TODO: [cont] BUT Timer is uninitialized and thus crashes the app
        companion object {

        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val  wakeUpTime= (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            //what happens when alarms goes off
            val intent = Intent(context, TimerExpiredReciever::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)

            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReciever::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }


    enum class TimerState {
        STOPPED, PAUSED, RUNNING
    }

    enum class TimerMode {
        POMODORO, SHORT_BREAK, LONG_BREAK
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.STOPPED

    private var secondsRemaining: Long = 0

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
        //title of task selected
        val titletask = view.findViewById<TextView>(R.id.timertask_name)

        if(PrefUtil.getCurrentRunningTask(context!!).isNotEmpty())
        {
            val taskjson = PrefUtil.getCurrentRunningTask(context!!)
            val task = Gson().fromJson<Task>(taskjson, object : TypeToken<Task>() {}.type)

            // set the name of the timer in the timer screen to the specific selected task name
            titletask.text = "Task : " + task.name

            titletask.visibility = View.VISIBLE

        }

         if(PrefUtil.getCurrentRunningTask(context!!).isEmpty() && titletask.visibility != View.INVISIBLE)
        {
            titletask.visibility = View.INVISIBLE
        }

        fab_start.setOnClickListener { v ->
            startTimer()
            timerState = TimerState.RUNNING
            updateButtons()
        }

        fab_pause.setOnClickListener { v ->
            timer.cancel()
            timerState = TimerState.PAUSED
            updateButtons()
        }

        fab_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }

        updateButtons()
    }

    override fun onStop() {
        super.onStop()
        onTimerFinished()
    }

    override fun onResume() {
        super.onResume()

        initTimer()
        removeAlarm(context!!)
//        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.RUNNING) {
            timer.cancel()
            val wakeUpTime = setAlarm(context!!, nowSeconds,secondsRemaining)
        }


        //persist the timer data to use on resume of app
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, context as Activity)
        PrefUtil.setSecondsRemaining(secondsRemaining, context as Activity)
        PrefUtil.setTimerState(timerState, context as Activity)
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(context as Activity)

        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was running in the background
        if (timerState == TimerState.STOPPED)
            setNewTimerLength()
        else
            setPreviousTimerLength()


        secondsRemaining = if (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED)
            PrefUtil.getSecondsRemaining(context as Activity)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(context!!)

        //Subtract the secondsRemaining from before the alarm was set
        // by the seconds passed during running the background alarm
        if(alarmSetTime>0)
            secondsRemaining -= nowSeconds - alarmSetTime


        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.RUNNING)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer() {
        timerState = TimerState.RUNNING

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
        timerState = TimerState.STOPPED

        evaluateTask()
        updateMode()
        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength()

        progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, context as Activity)
        secondsRemaining = timerLengthSeconds


        updateButtons()
        updateCountdownUI()
    }

    private fun evaluateTask() {
        val currentTask = PrefUtil.getCurrentRunningTask(context!!)

        val data = PrefUtil.getTask_List(context!!)

        val gson = Gson()

        val type = object : TypeToken<ArrayList<Task>>() {}.type
        val dataAsArray = gson.fromJson<ArrayList<Task>>(data,type)

        var taskname = ""
        //bug here
        if(currentTask.isEmpty()){
            taskname = "Other" //TODO: SET CURRENT TASK AVAILABLE
        }else{
            val taskTemp = gson.fromJson<Task>(currentTask, object : TypeToken<Task>() {}.type)
            taskname = taskTemp.name
        }

        for(t in dataAsArray){
            if(t.name == taskname){
                if(secondsRemaining <= 0)
                {
                    t.incrementDone()
                    //TODO: increase daily done task count AND check if we reached daily goal AND notify user
                }else{
                    t.incrementSkip()
                }
            }
        }

        val arrayAsJson= gson.toJson(dataAsArray)
        Log.i("FINISHED TASK",arrayAsJson)

        // persist the data
        PrefUtil.setTaskList(arrayAsJson, context!!)
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(context as Activity)
        timerLengthSeconds = lengthInMinutes * 60L
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(context as Activity)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinutesUntilFinished = secondsRemaining - (minutesUntilFinished * 60)
        val secondsStr = secondsInMinutesUntilFinished.toString()

        countdown.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateMode(){
        val mode = PrefUtil.getTimerMode(context!!)
        val count = PrefUtil.getTaskDoneCount(context!!)

        if(mode == TimerMode.POMODORO)
        {
            Log.i("COUNT FRAGMENT", count.toString())
            Log.i("COUNT mode", mode.toString())
            if(count%PrefUtil.getCountBeforeLongBreak(context!!) == 0)
                PrefUtil.setTimerMode(TimerMode.LONG_BREAK, context!!)
            else
                PrefUtil.setTimerMode(TimerMode.SHORT_BREAK,context!!)
        }else{
            PrefUtil.setTimerMode(TimerMode.POMODORO,context!!)
        }

    }

    private fun updateButtons() {
        when (timerState) {
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
}