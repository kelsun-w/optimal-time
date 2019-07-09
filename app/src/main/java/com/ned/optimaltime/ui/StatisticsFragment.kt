package com.ned.optimaltime.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.ned.optimaltime.R

class StatisticsFragment : Fragment() {

    val gson: Gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.stats_fragment, container, false)
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
//    {
//        super.onViewCreated(view, savedInstanceState)
//
//        total_task_done.text = TimerUtil.getTaskDoneCount(context!!).toString()
//        total_task_skipped.text = TimerUtil.getTaskSkippedCount(context!!).toString()
//        displayDataAsPie()
//    }
//
//    fun displayDataAsPie(){
//        val pieChart: PieChart = pieChart
//
//        val entries : ArrayList<PieEntry> = ArrayList<PieEntry>()
//
//        val datasetAsJson = TimerUtil.getTask_List(context!!)
//        val type = object: TypeToken<ArrayList<Task>>() {}.type
//
//        val dataset : ArrayList<Task> = gson.fromJson<ArrayList<Task>>(datasetAsJson, type)
//
//        var total = 0
//        val dataset_real = ArrayList<Task>()
//
//        for(x in dataset){
//            if(x.done > 0){
//                total += x.done
//                dataset_real.add(x)
//            }
//        }
//
//        if(dataset_real.isEmpty()){
//            entries.add(PieEntry(0f,"EMPTY"))
//        }else{
//            for(x in dataset_real){
//                val percentage =  x.done/total.toFloat() * 100
////                Log.i("added to pie",x.name+" : "+percentage.toString())
//                entries.add(PieEntry(percentage,x.name))
//            }
//        }
//
//        val set : PieDataSet = PieDataSet(entries, "")
//        set.setColors(mutableListOf(resources.getColor(R.color.a1),resources.getColor(R.color.a2),resources.getColor(
//            R.color.a3
//        ),resources.getColor(R.color.a4),resources.getColor(R.color.a5),resources.getColor(
//            R.color.a6
//        )))
//
//        pieChart.setHoleColor(Color.TRANSPARENT)
//        pieChart.setHoleRadius(20f)
//
//        //value text and label text
//        val pieData : PieData = PieData(set)
//        pieData.setValueTextSize(15f)
//        pieData.setValueTextColor(Color.WHITE)
//        pieChart.setEntryLabelTextSize(15f)
//
//        //legend
//        val legend = pieChart.legend
//        legend.textColor = Color.WHITE
//        legend.textSize = 17f
//
//        //description
//        val description = pieChart.description
//        description.isEnabled = false
//
//        pieChart.isRotationEnabled = false
//        pieChart.data = pieData
//        pieChart.invalidate()
//    }
}