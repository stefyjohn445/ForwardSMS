package com.example.pollserverforwardapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.R

class SlotAdapter(private val slotList: List<String>,
                  private val itemClickListener: SlotItemClickListener) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_slots, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slotList[position]
        holder.bind(slot)
        holder.itemView.setOnClickListener {
            itemClickListener.onSlotItemClick(slot)
        }
    }

    override fun getItemCount(): Int {
        return slotList.size
    }

    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val slotTextView: TextView = itemView.findViewById(R.id.slotTextView)

        fun bind(slot: String) {
            slotTextView.text = slot
        }
    }

    interface SlotItemClickListener {
        fun onSlotItemClick(selectedSlot: String)
    }
}
