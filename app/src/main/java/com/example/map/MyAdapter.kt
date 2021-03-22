package com.example.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.map.DB.DatabaseORMModel
import java.text.SimpleDateFormat
import java.util.*

class MyAdapter(): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val listItem = mutableListOf<DatabaseORMModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutId: Int = R.layout.list_item
        val item = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return MyViewHolder(item)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = listItem[position]

        holder.apply {
            tvDate.text = item.date
            tvDayOfWeek.text = item.dayOfWeek
            tvStart.text = "Вошёл: ${item.start}"
            tvEnd.text = "Вышел: ${item.end}"
            tvName.text = item.name
            tvTime.text = item.time
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
        val tvDayOfWeek: TextView = itemView.findViewById(R.id.tv_day_of_week)
        val tvStart: TextView = itemView.findViewById(R.id.tv_start)
        val tvEnd: TextView = itemView.findViewById(R.id.tv_end)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    }

}