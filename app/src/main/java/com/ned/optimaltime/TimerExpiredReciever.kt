package com.ned.optimaltime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ned.optimaltime.util.PrefUtil

class TimerExpiredReciever : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtil.setTimerState(TimerFragment.TimerState.STOPPED,context)
        PrefUtil.setAlarmSetTime(0,context)
    }
}
