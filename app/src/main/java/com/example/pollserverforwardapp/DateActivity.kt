package com.example.pollserverforwardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.adapters.DateAdapter
import com.example.pollserverforwardapp.network.RetrofitApiClient
import com.example.pollserverforwardapp.network.RetrofitApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class DateActivity : AppCompatActivity(), DateAdapter.DateItemClickListener {
    companion object{
        val apiInterface : RetrofitApiInterface = RetrofitApiClient.getUpload().create(
            RetrofitApiInterface::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date)

        apiInterface.getDateHistory().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val datesList = response.body() ?: emptyList()
                    println("response--datesList $datesList")

                    // Now, you have the datesList, set it to your RecyclerView adapter
                    // Initialize RecyclerView, set LayoutManager, and set adapter
                    val recyclerView: RecyclerView = findViewById(R.id.dateRV)
                    val adapter = DateAdapter(datesList,this@DateActivity)
                    recyclerView.layoutManager = LinearLayoutManager(this@DateActivity)
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
    private fun generateDateList(): List<String> {
        // Replace this with your logic to generate the list of dates
        val dates = mutableListOf<String>()
        // For example, adding some dates
        for (i in 1..20) {
            dates.add("Date $i")
        }
        return dates
    }

    private fun navigateToSlotsScreen(selectedDate: String) {
        // Replace SlotsActivity::class.java with your actual slots screen activity
        val intent = Intent(this, SlotsActivity::class.java)
        intent.putExtra("selectedDate", selectedDate)
        startActivity(intent)
    }

    override fun onDateItemClick(selectedDate: String) {
        val intent = Intent(this, SlotsActivity::class.java)
        intent.putExtra("selectedDate", selectedDate)
        startActivity(intent)
    }
}