package com.example.pollserverforwardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.adapters.DateAdapter
import com.example.pollserverforwardapp.adapters.PatientStatusAdapter
import com.example.pollserverforwardapp.models.HistoryStatus
import com.example.pollserverforwardapp.network.RetrofitApiClient
import com.example.pollserverforwardapp.network.RetrofitApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientStatusActivity : AppCompatActivity(), PatientStatusAdapter.PatientStatusItemClickListener {

    companion object{
        val apiInterface : RetrofitApiInterface = RetrofitApiClient.getUpload().create(
            RetrofitApiInterface::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_status)
        val selectedSlot: String? = intent.getStringExtra("selectedSlot")
        val selectedDate: String? = intent.getStringExtra("selectedDate")
        println("selectedSlot----patient--$selectedSlot")
        println("selectedDate----patient--$selectedDate")

        if (selectedDate != null && selectedSlot != null) {
                apiInterface.getPatientStatusHistory(selectedDate,selectedSlot).enqueue(object : Callback<List<HistoryStatus>> {
                    override fun onResponse(call: Call<List<HistoryStatus>>, response: Response<List<HistoryStatus>>) {
                        if (response.isSuccessful) {
                            println("patientStatusResponse---body--${response.body()}")
                            val datesList = response.body() ?: emptyList()
                            println("patientStatusResponse-----$datesList")
                            // Now, you have the datesList, set it to your RecyclerView adapter
                            // Initialize RecyclerView, set LayoutManager, and set adapter
                            val recyclerView: RecyclerView = findViewById(R.id.patientStatusRV)
                            val adapter = PatientStatusAdapter(datesList, this@PatientStatusActivity)
                            recyclerView.layoutManager = LinearLayoutManager(this@PatientStatusActivity)
                            recyclerView.adapter = adapter
                        } else {
                            // Handle API call failure
                        }
                    }

                    override fun onFailure(call: Call<List<HistoryStatus>>, t: Throwable) {
                        // Handle failure
                    }
                })
        }else {
            // Handle the case where selectedSlot or selectedDate is null
        }
    }

    private fun generatePatientStatusList(): List<String> {
        // Replace this with your logic to generate the list of dates
        val patients = mutableListOf<String>()
        // For example, adding some patients
        for (i in 1..20) {
            patients.add("Patient $i")
        }
        return patients
    }

    private fun navigateToSlotsScreen(selectedPatient: String) {
        // Replace SlotsActivity::class.java with your actual slots screen activity
        val intent = Intent(this, SlotsActivity::class.java)
        intent.putExtra("selectedPatient", selectedPatient)
        startActivity(intent)
    }
    override fun onPatientStatusItemClick(selectedPatient: String) {
        val intent = Intent(this, SlotsActivity::class.java)
        intent.putExtra("selectedPatient", selectedPatient)
        startActivity(intent)
    }
}