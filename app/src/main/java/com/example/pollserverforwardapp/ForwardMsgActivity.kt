package com.example.pollserverforwardapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pollserverforwardapp.models.ImageWithDataUploadResponse
import com.example.pollserverforwardapp.models.Patient

class ForwardMsgActivity : AppCompatActivity() {
    private lateinit var forwardSmsButton: Button
    private lateinit var responseData: ImageWithDataUploadResponse
    private val SEND_SMS_PERMISSION_REQUEST_CODE = 1001
    private lateinit var nameTV: TextView
    private lateinit var timeTV: TextView
    private lateinit var dateTV: TextView
    private lateinit var msgTV: TextView
    private lateinit var forwardSms: Button
    private lateinit var nameAndPhoneNumbers: List<Patient>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forward_msg)

        val responseData = intent.getSerializableExtra("responseData") as? ImageWithDataUploadResponse
        if (responseData != null) {
            println("responseData---$responseData")
            nameTV= findViewById(R.id.nameTV)
            timeTV = findViewById(R.id.timeTV)
            dateTV = findViewById(R.id.dateTV)
            msgTV = findViewById(R.id.msgTV)
            forwardSmsButton = findViewById(R.id.forwardSms)
            nameTV.text = responseData.doctor_name
            timeTV.text = responseData.time
            dateTV.text = responseData.date
            msgTV.text = responseData.message
            nameAndPhoneNumbers = responseData.data

            forwardSmsButton.setOnClickListener {
                if (isSmsPermissionGranted()) {
                    // Permission is granted, proceed with sending SMS
                    sendSms(responseData)
                } else {
                    // Permission is not granted, request it
                    requestSmsPermission()
                }
            }
        } else {
            // Handle the case where data retrieval failed
        }
    }

    private fun sendSms(responseData: ImageWithDataUploadResponse) {
        try {
            val smsManager = SmsManager.getDefault()
           val message = responseData.message
            for (nameAndPhoneNumbers in nameAndPhoneNumbers) {
                val number = nameAndPhoneNumbers.number
                val name = nameAndPhoneNumbers.name
                if (number != null) {
                    smsManager.sendTextMessage(number, null, message, null, null)
                }
            }
            Toast.makeText(this@ForwardMsgActivity, "Messages Sent", Toast.LENGTH_LONG).show()
        } catch (ex: Exception) {
            Toast.makeText(this@ForwardMsgActivity, ex.message.toString(), Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        }
    }

    private fun isSmsPermissionGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        requestPermissions(arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending SMS
                sendSms(responseData)
            } else {
                // Permission denied, inform the user or disable the SMS feature
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}