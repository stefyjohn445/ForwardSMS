package com.example.pollserverforwardapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeScreen : AppCompatActivity() {
    private lateinit var viewHistoryButton: Button
    private lateinit var sendRemainderButton: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        viewHistoryButton = findViewById(R.id.viewHistoryButton)
        sendRemainderButton = findViewById(R.id.sendRemainderButton)

        viewHistoryButton.setOnClickListener{
            val intent = Intent(this@HomeScreen, DateActivity::class.java)
            startActivity(intent)
        }

        sendRemainderButton.setOnClickListener{
            val intent = Intent(this@HomeScreen, MainActivity::class.java)
            startActivity(intent)
        }
    }
}