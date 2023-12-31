package com.example.pollserverforwardapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.R
import com.example.pollserverforwardapp.models.Patient

class ContactAdapter(private val contacts: List<Patient>,
                     private val contactEditListener: ContactEditListener) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
//        holder.setIsRecyclable(false)
//        hasStableIds()
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    interface ContactEditListener {
//        fun onContactEdit(patient: Patient, position: Int)
    fun onContactEdit(updatedName: String, updatedNumber: String, position: Int)
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        private val numberTextView: TextView = itemView.findViewById(R.id.textViewNumber)

        fun bind(patient: Patient) {
            nameTextView.text = patient.name
            numberTextView.text = patient.number
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
//                    contactEditListener.onContactEdit(contacts[position], position)
                    val patient = contacts[position]
                    contactEditListener.onContactEdit(patient.name, patient.number, position)
                }
            }
        }
    }
}
