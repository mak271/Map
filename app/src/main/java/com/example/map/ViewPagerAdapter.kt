package com.example.map

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*

class ViewPagerAdapter: RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder>() {

    private val listItem = mutableListOf<LineGraphSeries<DataPoint>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val numberItem = R.layout.graph_page
        val item = LayoutInflater.from(parent.context).inflate(numberItem, parent, false)
        return MyViewHolder(item)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            graphView.addSeries(listItem[position])

            if (position == 0) {
                customizeGraphView1()
            }

            if (position == 1) {
                customizeGraphView2()
            }

        }


    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    fun initList(list: List<LineGraphSeries<DataPoint>>) {
        listItem.clear()
        listItem.addAll(list)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var graphView: GraphView = itemView.findViewById(R.id.graphView)

        fun customizeGraphView1() {
            graphView.title = "Посещаемость за последнюю неделю"
            graphView.viewport.apply {
                isXAxisBoundsManual = true
                isYAxisBoundsManual = true
                setMinX(MainActivity.points1.first()?.x!!)
                setMaxX(MainActivity.points1.last()?.x!!)
                setMinY(0.0)
                setMaxY(24.0)
            }

            graphView.gridLabelRenderer.apply {
                numHorizontalLabels = 7
                setHumanRounding(false)
                gridStyle = GridLabelRenderer.GridStyle.NONE
                isVerticalLabelsVisible = false
                textSize = 25f

                val day = SimpleDateFormat("EEEE", Locale.US)
                labelFormatter = object : DefaultLabelFormatter() {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun formatLabel(value: Double, isValueX: Boolean): String {
                        if (isValueX) {
                            return day.format(Date(value.toLong())).substring(0, 4)
                        } else
                            return super.formatLabel(value, isValueX)
                    }
                }
            }






        }

        fun customizeGraphView2() {

            graphView.title = "Посещаемость за последний месяц"
            graphView.viewport.apply {
                isXAxisBoundsManual = true
                isYAxisBoundsManual = true
                setMinX(MainActivity.points2.first()?.x!!)
                setMaxX(MainActivity.points2.last()?.x!!)
                setMinY(0.0)
                setMaxY(24.0)
            }

            graphView.gridLabelRenderer.apply {
                numHorizontalLabels = 15
                setHumanRounding(false)
                textSize = 20f
                gridStyle = GridLabelRenderer.GridStyle.NONE
                isVerticalLabelsVisible = false

                val day = SimpleDateFormat("dd", Locale.US)
                labelFormatter = object : DefaultLabelFormatter() {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun formatLabel(value: Double, isValueX: Boolean): String {
                        if (isValueX) {
                            return day.format(Date(value.toLong()))
                        } else
                            return super.formatLabel(value, isValueX)
                    }
                }
            }


        }

    }



}