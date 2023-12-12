package com.example.pollserverforwardapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.R

class DateAdapter(private val dateList: List<String>,
                  private val itemClickListener: DateItemClickListener) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dateList[position]
        holder.bind(date)
        holder.itemView.setOnClickListener {
            itemClickListener.onDateItemClick(date)
        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        fun bind(date: String) {
            dateTextView.text = date
        }
    }

    interface DateItemClickListener {
        fun onDateItemClick(selectedDate: String)
    }
}
