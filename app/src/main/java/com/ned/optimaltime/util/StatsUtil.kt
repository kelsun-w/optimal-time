package com.ned.optimaltime.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.ned.optimaltime.other.ChartSortMode
import com.ned.optimaltime.other.PieChartMode

/***
 * Wrapper class for storing values in DefaultSharedPreferences that are related to the Statistics Fragment
 */
class StatsUtil {
    companion object {
        private const val SORT_HISTORY_BY_ID = "com.ned.optimaltime.stats.sort_mode"
        private const val PIECHART_MODE_ID = "com.ned.optimaltime.stats.piechart_mode"

        fun setHistorySortMode(mode: ChartSortMode, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(SORT_HISTORY_BY_ID, mode.ordinal)
            editor.apply()
        }

        fun getHistorySortMode(context: Context): ChartSortMode {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(SORT_HISTORY_BY_ID, 0)
            return ChartSortMode.values()[ordinal]
        }

        fun setPieMode(mode: PieChartMode, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(PIECHART_MODE_ID, mode.ordinal)
            editor.apply()
        }

        fun getPieMode(context: Context): PieChartMode {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(PIECHART_MODE_ID, 0)
            return PieChartMode.values()[ordinal]
        }
    }
}