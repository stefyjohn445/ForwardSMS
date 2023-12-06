package com.example.pollserverforwardapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pollserverforwardapp.adapters.ContactAdapter
import com.example.pollserverforwardapp.models.ImageWithDataUploadResponse
import com.example.pollserverforwardapp.models.Patient
import com.google.gson.Gson

class ForwardMsgActivity : AppCompatActivity(), ContactAdapter.ContactEditListener {
    private lateinit var forwardSmsButton: Button
    private lateinit var responseData: ImageWithDataUploadResponse
    private val SEND_SMS_PERMISSION_REQUEST_CODE = 1001
    private lateinit var nameTV: TextView
    private lateinit var timeTV: TextView
    private lateinit var dateTV: TextView
    private lateinit var msgTV: TextView
    private lateinit var forwardSms: Button
    private lateinit var nameAndPhoneNumbers: List<Patient>
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter

//    private lateinit var nameAndPhoneNumbers: MutableList<Patient>
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forward_msg)
        nameAndPhoneNumbers = emptyList()

//    nameAndPhoneNumbers = responseData.data.toMutableList()

        val responseData = intent.getSerializableExtra("responseData") as? ImageWithDataUploadResponse
        if (responseData != null) {
            println("responseData---$responseData")
            nameTV= findViewById(R.id.nameTV)
            timeTV = findViewById(R.id.timeTV)
            dateTV = findViewById(R.id.dateTV)
            msgTV = findViewById(R.id.msgTV)
            forwardSmsButton = findViewById(R.id.forwardSms)
            nameTV.text = responseData.doctor
            timeTV.text = responseData.slot
            dateTV.text = responseData.date
            msgTV.text = responseData.message
            nameAndPhoneNumbers = responseData.data
            println("responseData contacts--$responseData.nameAndPhoneNumbers")
            recyclerView = findViewById(R.id.contactRV)
            recyclerView.layoutManager = LinearLayoutManager(this)

            contactAdapter = ContactAdapter(nameAndPhoneNumbers, this)
            recyclerView.adapter = contactAdapter

//            contactAdapter = ContactAdapter(nameAndPhoneNumbers)
//            recyclerView.adapter = contactAdapter
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
            val smsManager: SmsManager = SmsManager.getDefault()
           val message = responseData.message
            val url = responseData.url
            for (nameAndPhoneNumbers in nameAndPhoneNumbers) {
                val number = nameAndPhoneNumbers.number
                val name = nameAndPhoneNumbers.name
                val patient_id = nameAndPhoneNumbers.patient_id
                val messageURL = "$message$url$patient_id"
                println("messageURL---$messageURL")
                    if (number != null) {
                    println("url-------$url")
                    println("url-message-----$messageURL")
                    smsManager.sendTextMessage(number, null, messageURL, null, null)
                    Toast.makeText(this@ForwardMsgActivity, "Messages Sent", Toast.LENGTH_LONG).show()
                }
            }
        }catch (ex: SecurityException) {
            // Handle SecurityException (Permission Issue)
            Toast.makeText(this@ForwardMsgActivity, "Permission Denied", Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        } catch (ex: Exception) {
            Toast.makeText(this@ForwardMsgActivity, ex.message.toString(), Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        }
    }

    private fun isSmsPermissionGranted(): Boolean {
//        return checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
//        requestPermissions(arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS),
            SEND_SMS_PERMISSION_REQUEST_CODE
        )
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

    @SuppressLint("MissingInflatedId")
//    override fun onContactEdit(patient: Patient, position: Int) {
//        // Handle editing of the patient details here
//        // For example, open an edit dialog or activity
//        // Update the 'contacts' list in the adapter after editing
//        // For example:
//        // contacts[position] = updatedPatientDetails
//        // contactAdapter.notifyItemChanged(position)
////
//        val dialog = AlertDialog.Builder(this@ForwardMsgActivity)
//        val dialogView = layoutInflater.inflate(R.layout.edit_patient_dialog, null)
//        dialog.setView(dialogView)
//
//        val editedName = dialogView.findViewById<EditText>(R.id.editedName)
//        val editedNumber = dialogView.findViewById<EditText>(R.id.editedNumber)
//
//        // Populate dialog fields with existing patient details
//        editedName.setText(patient.name)
//        editedNumber.setText(patient.number)
//
//        dialog.setPositiveButton("Save") { _, _ ->
//            val updatedName = editedName.text.toString()
//            val updatedNumber = editedNumber.text.toString()
//
//            // Update the patient details in the contacts list
//            contacts[position].name = updatedName
//            contacts[position].number = updatedNumber
//
//            // Notify the adapter of the change
//            contactAdapter.notifyItemChanged(position)
//        }
//
//        dialog.setNegativeButton("Cancel") { _, _ ->
//            // Handle cancel action if needed
//        }
//
//        dialog.show()
//    }

    override fun onContactEdit(updatedName: String, updatedNumber: String, position: Int) {
        // Handle editing of the patient details here
        // For example, open an edit dialog or activity
        // Update the 'contacts' list in the adapter after editing
        // For example:
        // contacts[position] = updatedPatientDetails
        // contactAdapter.notifyItemChanged(position)
//
        val dialog = AlertDialog.Builder(this@ForwardMsgActivity)
        val dialogView = layoutInflater.inflate(R.layout.edit_patient_dialog, null)
        dialog.setView(dialogView)

        val editedName = dialogView.findViewById<EditText>(R.id.editedName)
        val editedNumber = dialogView.findViewById<EditText>(R.id.editedNumber)

//        // Populate dialog fields with existing patient details
//        editedName.setText(patient.name)
//        editedNumber.setText(patient.number)
        editedName.setText(updatedName)
        editedNumber.setText(updatedNumber)
//
        dialog.setPositiveButton("Save") { _, _ ->
            val updatedName = editedName.text.toString()
            val updatedNumber = editedNumber.text.toString()
//
//            // Update the patient details in the contacts list
            nameAndPhoneNumbers[position].name = updatedName
            nameAndPhoneNumbers[position].number = updatedNumber
//
//            // Notify the adapter of the change
            contactAdapter.notifyItemChanged(position)
        }

        dialog.setNegativeButton("Cancel") { _, _ ->
            // Handle cancel action if needed
        }
//
        dialog.show()
    }
}

