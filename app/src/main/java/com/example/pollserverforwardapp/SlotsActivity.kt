package com.example.pollserverforwardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.adapters.DateAdapter
import com.example.pollserverforwardapp.adapters.SlotAdapter
import com.example.pollserverforwardapp.network.RetrofitApiClient
import com.example.pollserverforwardapp.network.RetrofitApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlotsActivity : AppCompatActivity(), SlotAdapter.SlotItemClickListener {
    companion object{
        val apiInterface : RetrofitApiInterface = RetrofitApiClient.getUpload().create(
            RetrofitApiInterface::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slots)

        val specificDate: String? = intent.getStringExtra("selectedDate")

        if (specificDate != null) {
            apiInterface.getSlotHistory(specificDate).enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val slotList = response.body() ?: emptyList()

                        // Now, you have the datesList, set it to your RecyclerView adapter
                        // Initialize RecyclerView, set LayoutManager, and set adapter
                        val recyclerView: RecyclerView = findViewById(R.id.slotsRV)
                        val adapter = SlotAdapter(slotList, this@SlotsActivity)
                        recyclerView.layoutManager = GridLayoutManager(this@SlotsActivity, 3)
                        recyclerView.adapter = adapter
                    } else {
                        // Handle API call failure
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }

    private fun generateSlotList(): List<String> {
        // Replace this with your logic to generate the list of slots
        val slots = mutableListOf<String>()
        // For example, adding some slots
        for (i in 1..20) {
            slots.add("Slot $i")
        }
        return slots
    }

    private fun navigateToSlotsScreen(selectedSlot: String) {
        // Replace SlotsActivity::class.java with your actual slots screen activity
        val intent = Intent(this, SlotsActivity::class.java)
        intent.putExtra("selectedSlot", selectedSlot)
        startActivity(intent)
    }

    override fun onSlotItemClick(selectedSlot: String) {
        val selectedDate: String? = intent.getStringExtra("selectedDate")
        println("selectedDate---$selectedDate")

        if (selectedDate != null) {
            val intent = Intent(this, PatientStatusActivity::class.java)
            intent.putExtra("selectedSlot", selectedSlot)
            intent.putExtra("selectedDate", selectedDate)

            println("selectedSlot----$selectedSlot")
            println("selectedDate----$selectedDate")

            startActivity(intent)
        } else {
            // Handle the case where specificDate is null or not provided
        }
    }
}