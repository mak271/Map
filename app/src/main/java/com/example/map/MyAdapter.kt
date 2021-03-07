package com.example.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MyAdapter(): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val listItem = mutableListOf<DatabaseORMModel>()

    val calendar: Calendar = Calendar.getInstance()
    val day = SimpleDateFormat("EEEE")
    val sdf = SimpleDateFormat("dd.MM.y")
    val formatted_day: String = day.format(calendar.time)
    val formatted_date: String = sdf.format(calendar.time)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutId: Int = R.layout.list_item
        val item = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return MyViewHolder(item)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = listItem[position]

        holder.apply {
            tvDate.text = "$formatted_date    $formatted_day"
            tvStart.text = item.start
            tvEnd.text = item.end
        }

    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    fun initList(list: List<DatabaseORMModel>) {
        listItem.clear()
        listItem.addAll(list)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvStart: TextView = itemView.findViewById(R.id.tv_start)
        val tvEnd: TextView = itemView.findViewById(R.id.tv_end)
    }

}