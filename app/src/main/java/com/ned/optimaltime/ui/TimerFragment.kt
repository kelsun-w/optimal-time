package com.ned.optimaltime.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ned.optimaltime.R
import com.ned.optimaltime.util.TimerUtil
import java.util.*

class TimerFragment : Fragment() {
    //TODO: After deleting a CURRENT RUNNING TASK change the pointer to another task
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
//
//    enum class TimerState {
//        STOPPED, PAUSED, RUNNING
//    }
//
//    enum class TimerMode {
//        POMODORO, SHORT_BREAK, LONG_BREAK
//    }
//
//    private lateinit var timer: CountDownTimer
//    private var timerLengthSeconds: Long = 0
//    private var timerState = TimerState.STOPPED
//
//    private var secondsRemaining: Long = 0
//
//    private lateinit var mode : TimerMode
//    private var taskDone = 0
//    private var taskSkipped = 0
//    private lateinit var taskList: ArrayList<Task>
//
//    //Json serialization stuffs
//    val gson : Gson = Gson()
//    val task_type = object : TypeToken<Task>() {}.type
//    val array_task_type = object:TypeToken<ArrayList<Task>>() {}.type
//
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.timer_fragment, container, false)
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        //title of task selected
//        val titletask = view.findViewById<TextView>(R.id.timertask_name)
//
//        //get the task done count when screen get loaded at first
//        taskDone = TimerUtil.getTaskDoneCount(context!!)
//        mode = TimerUtil.getTimerMode(context!!)
//        taskSkipped = TimerUtil.getTaskSkippedCount(context!!)
//
//        if (TimerUtil.getCurrentRunningTask(context!!).isNotEmpty()) {
//            val taskjson = TimerUtil.getCurrentRunningTask(context!!)
//            val task = gson.fromJson<Task>(taskjson, task_type)
//
//            // set the name of the timer in the timer screen to the specific selected task name
//            titletask.text = "Task : " + task.name
//
//        }else{
//            val dataset = Gson().fromJson<ArrayList<Task>>(TimerUtil.getTask_List(context!!),array_task_type)
//            for(x in dataset){
//                if(x.name == "Other")
//                {
//                    TimerUtil.setCurrentRunningTask(gson.toJson(x),context as Activity)
//                }
//            }
//            titletask.text = "Task : Other"
//        }
//
//        if(mode == TimerMode.POMODORO){
//            break_msg.visibility = View.INVISIBLE
//        }else{
//            break_msg.visibility = View.VISIBLE
//        }
//
//        fab_debug.setOnClickListener {v ->
//            if (::timer.isInitialized)
//                debugFinish()   //For saving time while debugging
//        }
//        fab_start.setOnClickListener { v ->
//            Log.i("btn click", "start!")
//            startTimer()
//            updateButtons()
//        }
//
//        fab_pause.setOnClickListener { v ->
//            Log.i("btn click", "pause!")
//            timer.cancel()
//            timerState = TimerState.PAUSED
//            updateButtons()
//        }
//
//        fab_stop.setOnClickListener { v ->
//            Log.i("btn click", "stop!")
//            if (::timer.isInitialized)
//                timer.cancel()
//
//            onTimerFinished()
//        }
//
//        updateButtons()
//        Log.i("State", timerState.toString())
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.i("task",taskDone.toString())
//
//        taskDone = TimerUtil.getTaskDoneCount(context!!)
//        taskSkipped = TimerUtil.getTaskSkippedCount(context!!)
//        mode = TimerUtil.getTimerMode(context as Activity)
//        initTimer()
//        removeAlarm(context!!)
//    }
//
//    override fun onPause() {
//        super.onPause()
//
//        if (timerState == TimerState.RUNNING) {
//            timer.cancel()
//            val wakeUpTime = setAlarm(
//                context!!,
//                nowSeconds,
//                secondsRemaining
//            )
//        }
//
//        //persist the timer data to use on resume of app
//        TimerUtil.setTimerMode(mode,context as Activity)
//
//        TimerUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, context as Activity)
//        TimerUtil.setSecondsRemaining(secondsRemaining, context as Activity)
//        TimerUtil.setTimerState(timerState, context as Activity)
//    }
//
//    private fun initTimer() {
//        timerState = TimerUtil.getTimerState(context as Activity)
//
//        //we don't want to change the length of the timer which is already running
//        //if the length was changed in settings while it was running in the background
//        if (timerState == TimerState.STOPPED)
//            setNewTimerLength()
//        else
//            setPreviousTimerLength()
//
//
//        secondsRemaining = if (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED)
//            TimerUtil.getSecondsRemaining(context as Activity)
//        else
//            timerLengthSeconds
//
//        val alarmSetTime = TimerUtil.getAlarmSetTime(context!!)
//
//        //Subtract the secondsRemaining from before the alarm was set
//        // by the seconds passed during running the background alarm
//        if (alarmSetTime > 0)
//            secondsRemaining -= nowSeconds - alarmSetTime
//
//
//        if (secondsRemaining <= 0)
//            onTimerFinished()
//        else if (timerState == TimerState.RUNNING)
//            startTimer()
//
//        updateButtons()
//        updateCountdownUI()
//    }
//
//    private fun startTimer() {
//        timerState = TimerState.RUNNING
//
//        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
//            override fun onFinish() {
//                onTimerFinished()
//            }
//
//            override fun onTick(millisUntilFinished: Long) {
//                secondsRemaining = millisUntilFinished / 1000
//                updateCountdownUI()
//            }
//        }.start()       //start the timer
//    }
//
//    private fun onTimerFinished() {
//        timerState = TimerState.STOPPED
//
//        if(TimerUtil.getTimerMode(context!!) == TimerMode.POMODORO) {
//            //TODO: Dialog box prompting user they want to go to break or continue work
//
//            evaluateTask()
//            if (secondsRemaining <= 0)
//                //if mode is WORK, update to BREAK only if we finished the full work length
//                updateMode()
//
//        } else {
//            //if mode is BREAK, we can just update to WORK
//            updateMode()
//        }
//
//        if(mode == TimerMode.POMODORO){
//            break_msg.visibility = View.INVISIBLE
//        }else{
//            break_msg.visibility = View.VISIBLE
//        }
//
//        //set the length of the timer to be the one set in SettingsActivity
//        //if the length was changed when the timer was running
//        setNewTimerLength()
//
//        progress_countdown.progress = 0
//
//        TimerUtil.setSecondsRemaining(timerLengthSeconds, context as Activity)
//        secondsRemaining = timerLengthSeconds
//
//
//        updateButtons()
//        updateCountdownUI()
//    }
//
//    /**
//     *  Makes changes to the data of current task and the overall data of tasks.
//     *  If this method was called when the timer expired, then `task done count` increases else `task skip count` increases
//     **/
//    private fun evaluateTask() {
//        val currentTask = TimerUtil.getCurrentRunningTask(context!!)
//
//        val data = TimerUtil.getTask_List(context!!)
//
//        val dataAsArray = gson.fromJson<ArrayList<Task>>(data, array_task_type)
//
//        var taskname = ""
//
//        if (currentTask.isEmpty()) {
//            taskname = "Other"
//        } else {
//            val taskTemp = gson.fromJson<Task>(currentTask, task_type)
//            taskname = taskTemp.name
//        }
//
//        for (t in dataAsArray) {
//            if (t.name == taskname) {
//                if (secondsRemaining <= 0) {
//                    t.incrementDone()
//                    taskDone++
//                    //TODO: increase daily done task count AND check if we reached daily goal AND notify user
//                } else {
//                    t.incrementSkip()
//                    taskSkipped++
//                }
//            }
//        }
//
//        val arrayAsJson = gson.toJson(dataAsArray)
//        Log.i("FINISHED TASK", arrayAsJson)
//
//        // persist the data
//        TimerUtil.setTaskList(arrayAsJson, context!!)
//        TimerUtil.setTaskDoneCount(taskDone,context as Activity)
//        TimerUtil.setTaskSkippedCount(taskSkipped,context as Activity)
//    }
//
//    private fun setNewTimerLength() {
//        val lengthInMinutes = TimerUtil.getTimerLength(context as Activity)
//        timerLengthSeconds = lengthInMinutes * 60L
//        progress_countdown.max = timerLengthSeconds.toInt()
//    }
//
//    private fun setPreviousTimerLength() {
//        timerLengthSeconds = TimerUtil.getPreviousTimerLengthSeconds(context as Activity)
//        progress_countdown.max = timerLengthSeconds.toInt()
//    }
//
//    private fun updateCountdownUI() {
//        val minutesUntilFinished = secondsRemaining / 60
//        val secondsInMinutesUntilFinished = secondsRemaining - (minutesUntilFinished * 60)
//        val secondsStr = secondsInMinutesUntilFinished.toString()
//
//        countdown.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
//        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
//    }
//
//    private fun updateMode() {
//        val count = TimerUtil.getTaskDoneCount(context!!)
//
//        if (mode == TimerMode.POMODORO) {
//            Log.i("COUNT FRAGMENT", count.toString())
//            Log.i("COUNT mode", mode.toString())
//            if (count % TimerUtil.getCountBeforeLongBreak(context!!) == 0)
//                mode = TimerMode.LONG_BREAK
//            else
//                mode = TimerMode.SHORT_BREAK
//        } else {
//            mode = TimerMode.POMODORO
//        }
//        TimerUtil.setTimerMode(mode,context as Activity)
//    }
//
//    private fun updateButtons() {
//        when (timerState) {
//            TimerState.RUNNING -> {
//                fab_start.isEnabled = false
//                fab_pause.isEnabled = true
//                fab_stop.isEnabled = true
//            }
//
//            TimerState.PAUSED -> {
//                fab_start.isEnabled = true
//                fab_pause.isEnabled = false
//                fab_stop.isEnabled = true
//            }
//
//            TimerState.STOPPED -> {
//                fab_start.isEnabled = true
//                fab_pause.isEnabled = false
//                fab_stop.isEnabled = false
//            }
//        }
//    }
//
//    private fun debugFinish(){
//        secondsRemaining = 0
//        timer.cancel()
//        onTimerFinished()
//    }
//
}