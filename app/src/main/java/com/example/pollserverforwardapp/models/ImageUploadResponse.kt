package com.example.pollserverforwardapp.models

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.io.Serializable

data class ImageWithDataUploadResponse (@SerializedName("doctor_name") val doctor_name: String,
                                        @SerializedName("date")val date: String, // Assuming the server sends back the uploaded image URL
                                        @SerializedName("time")val time: String,
                                        @SerializedName("message")val message: String,
                                        @SerializedName("data")val data: List<Patient>
    ): Serializable

data class Patient(
    val name: String,
    val number: String
): Serializable
