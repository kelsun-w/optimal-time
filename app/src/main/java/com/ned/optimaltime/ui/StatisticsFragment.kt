package com.ned.optimaltime.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.ned.optimaltime.R
import com.ned.optimaltime.other.ChartSortMode
import com.ned.optimaltime.other.Constants
import com.ned.optimaltime.util.StatsUtil
import com.ned.optimaltime.viewmodel.DataViewModel
import com.ned.optimaltime.vo.CustomBarChart
import com.ned.optimaltime.vo.DateCountFormat
import com.ned.optimaltime.vo.Task
import kotlinx.android.synthetic.main.stats_fragment.*

class StatisticsFragment : Fragment() {

    private lateinit var mViewModel: DataViewModel
    private lateinit var mBarChart: CustomBarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(com.ned.optimaltime.R.layout.stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(DataViewModel::class.java)

        val tab1 = statf_tab1           //scrollable bar for selecting task
        val tab2: TabLayout = statf_tab2    //sort chart data by

        //*****************************************************************************************
        var taskList : List<Task>? =  null

        //Filling the scrollable bar with current available tasks
        mViewModel.getAllTask().observe(this, Observer{ updatedList ->
            taskList = updatedList
            tab1.addTab(tab1.newTab().setText(Constants.STATF_ALLTASK_LABEL).setTag(Constants.STATF_ALLTASK_TAG))
            for (x in taskList!!) {
                tab1.addTab(tab1.newTab().setText(x.name).setTag(x.uid))
            }
        })

        tab1.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateHistoryChart()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        //*****************************************************************************************
        //Creating the tab layout for sorting chart data
        var selectedIndex = when (StatsUtil.getHistorySortMode(context!!)) {
            ChartSortMode.DAILY -> 0
            ChartSortMode.WEEKLY -> 1
            ChartSortMode.MONTHLY -> 2
        }

        tab2.getTabAt(selectedIndex)?.select()

        when (tab2.selectedTabPosition) {
            0 -> {
                ViewCompat.setBackground(
                    tab2.getTabAt(tab2.selectedTabPosition)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_start_selected)
                )
                ViewCompat.setBackground(
                    tab2.getTabAt(1)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_normal_unselected)
                )
                ViewCompat.setBackground(
                    tab2.getTabAt(2)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_end_unselected)
                )
            }
            1 -> {
                ViewCompat.setBackground(
                    tab2.getTabAt(0)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_start_unselected)
                )
                ViewCompat.setBackground(
                    tab2.getTabAt(tab2.selectedTabPosition)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_normal_selected)
                )
                ViewCompat.setBackground(
                    tab2.getTabAt(2)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_end_unselected)
                )
            }
            2 -> {
                ViewCompat.setBackground(
                    tab2.getTabAt(0)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_start_unselected)
                )
                ViewCompat.setBackground(
                    tab2.getTabAt(1)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_normal_unselected)
                )
                ViewCompat.setBackground(
                    tab2.getTabAt(tab2.selectedTabPosition)!!.view,
                    AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_end_selected)
                )
            }
        }

        tab2.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        ViewCompat.setBackground(
                            tab.view,
                            AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_start_selected)
                        )
                        StatsUtil.setHistorySortMode(ChartSortMode.DAILY, context!!)
                        updateHistoryChart()
                    }
                    1 -> {
                        ViewCompat.setBackground(
                            tab.view,
                            AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_normal_selected)
                        )
                        StatsUtil.setHistorySortMode(ChartSortMode.WEEKLY, context!!)
                        updateHistoryChart()
                    }
                    2 -> {
                        ViewCompat.setBackground(
                            tab.view,
                            AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_end_selected)
                        )
                        StatsUtil.setHistorySortMode(ChartSortMode.MONTHLY, context!!)
                        updateHistoryChart()
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> ViewCompat.setBackground(
                        tab.view,
                        AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_start_unselected)
                    )
                    1 -> ViewCompat.setBackground(
                        tab.view,
                        AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_normal_unselected)
                    )
                    2 -> ViewCompat.setBackground(
                        tab.view,
                        AppCompatResources.getDrawable(context!!, R.drawable.tab_layout_end_unselected)
                    )
                }
            }
        })
        //************************************************************************************
//        tab1.removeAllTabs()

        updateHistoryChart()
//        displayDataAsPie()
    }

    /**
     * Updates the history bar chart with new data depending on the currently set sorting method, DAILY, WEEKLY, MONTHLY.
     *  Then the bar chart is redrawn.
     */
    fun updateHistoryChart() {
        val chartData: List<DateCountFormat>
        val selected_tag : Long? = statf_tab1.getTabAt(statf_tab1.selectedTabPosition)?.tag?.toString()?.toLong()
        when (StatsUtil.getHistorySortMode(context!!)) {
             ChartSortMode.DAILY -> {
                chartData = if(selected_tag == Constants.STATF_ALLTASK_TAG ||selected_tag == null)
                    mViewModel.getDailyHistory()
                else
                    mViewModel.getDailyHistoryById(selected_tag)
            }
            ChartSortMode.WEEKLY -> {
                chartData = if(selected_tag == Constants.STATF_ALLTASK_TAG || selected_tag == null)
                    mViewModel.getWeeklyHistory()
                else
                    mViewModel.getWeeklyHistoryById(selected_tag)
            }
            ChartSortMode.MONTHLY -> {
                chartData = if(selected_tag == Constants.STATF_ALLTASK_TAG || selected_tag == null)
                    mViewModel.getMonthlyHistory()
                else
                    mViewModel.getMonthlyHistoryById(selected_tag)
            }
        }
        mBarChart = CustomBarChart(statfhistory_bar)
        mBarChart.updateData(chartData)
    }

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