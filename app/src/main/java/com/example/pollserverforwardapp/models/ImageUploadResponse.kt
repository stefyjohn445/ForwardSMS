package com.example.pollserverforwardapp.models

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.io.Serializable

data class ImageWithDataUploadResponse (@SerializedName("doctor") val doctor: String,
                                        @SerializedName("slot")val slot: String,
                                        @SerializedName("date")val date: String,
                                        @SerializedName("url")val url: String,
                                        @SerializedName("message")val message: String,
                                        @SerializedName("data")val data: List<Patient>
    ): Serializable

data class Patient(
    var name: String,
    var number: String,
    val patient_id: String
): Serializable

data class HistoryStatus( val name: String,
                          val number: String,
                          val status: String,
                          val patient_id: String): Serializable
