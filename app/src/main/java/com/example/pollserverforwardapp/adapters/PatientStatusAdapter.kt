package com.example.pollserverforwardapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.R
import com.example.pollserverforwardapp.models.HistoryStatus

class PatientStatusAdapter(private val patientStatusList: List<HistoryStatus>,
                           private val itemClickListener: PatientStatusItemClickListener) :
    RecyclerView.Adapter<PatientStatusAdapter.PatientStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientStatusViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_patient_status, parent, false)
        return PatientStatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientStatusViewHolder, position: Int) {
        val patientStatus = patientStatusList[position]
        holder.bind(patientStatus)
    }

    override fun getItemCount(): Int {
        return patientStatusList.size
    }

    class PatientStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val NameTextView: TextView = itemView.findViewById(R.id.NameTextView)
        private val numberTextView: TextView = itemView.findViewById(R.id.numberTextView)
        private val imageViewStatus: ImageView = itemView.findViewById(R.id.imageViewStatus)
        fun bind(historyStatus: HistoryStatus) {
            NameTextView.text = historyStatus.name
            numberTextView.text = historyStatus.number

            if(historyStatus.status == "Confirmed"){
                imageViewStatus.setImageResource(R.drawable.icons8_ok_48)
                imageViewStatus.visibility = View.VISIBLE
            }else if (historyStatus.status == "Rejected") {
                imageViewStatus.setImageResource(R.drawable.icons8_reject_48)
                imageViewStatus.visibility = View.VISIBLE
            } else {
                imageViewStatus.visibility = View.GONE
            }
        }
    }
    interface PatientStatusItemClickListener {
        fun onPatientStatusItemClick(selectedPatientStatus: String)
    }
}
