package com.ned.optimaltime.vo

import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.format.DateTimeFormatter


class CustomBarChart(val mChart: BarChart) {

    //constants
    var mLabel: String = "History"
    //
    lateinit var mSet: BarDataSet
    lateinit var mBarData: BarData

    private var mXLabel = ArrayList<String>()
    private var mEntries = ArrayList<BarEntry>()
    var mYMax: Float = 0f      //Max value of y-axis for given data set

    fun updateData(d: List<DateCountFormat>) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM uuuu HH mm")

        for (i in 0 until d.size) {
            val yVal = d[i].count.toFloat()
            mEntries.add(BarEntry(i.toFloat(), yVal))
            if (yVal > mYMax)
                mYMax = yVal

            val fstr = d[i].date.format(formatter)
            mXLabel.add(fstr)
            Log.d("onUpdate()", yVal.toString() + " " + fstr)
        }

        mSet = BarDataSet(mEntries, mLabel)
        mBarData = BarData(mSet)


        decorateGraph()
        mBarData.barWidth = 0.9f            //0.9 cuz 0.1 spacing between entries
        mBarData.setDrawValues(false)       //Dont draw the y value above entries

        mChart.data = mBarData
        mChart.setVisibleXRange(1f,4f)      //stack overflow 48124505

        mChart.invalidate()
    }

    private fun decorateGraph() {
        //x-axis
        val xAxis = mChart.xAxis
        xAxis.setDrawGridLines(false);

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.RED;
        xAxis.textSize = 10f
        xAxis.valueFormatter = IndexAxisValueFormatter(mXLabel)
        xAxis.labelCount = 4
        xAxis.axisLineWidth = 1.5f

        //Viewport
        mChart.setScaleEnabled(false)
        mChart.legend.isEnabled = false
        mChart.description.isEnabled = false

        //y-axis
        val yLeft = mChart.axisLeft
        yLeft.setDrawAxisLine(false)
        yLeft.granularity = 1f
        yLeft.axisMinimum = 0f
        yLeft.axisMaximum = mYMax + 1

        val yRight = mChart.axisRight
        yRight.setDrawLabels(false)
        yRight.setDrawAxisLine(false)
        yRight.granularity = 1f
        yRight.axisMinimum = 0f
        yRight.axisMaximum = mYMax + 1
    }
}