package com.example.pollserverforwardapp.network

import com.example.pollserverforwardapp.models.HistoryStatus
import com.example.pollserverforwardapp.models.ImageWithDataUploadResponse
import com.example.pollserverforwardapp.models.Message
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.Call

interface RetrofitApiInterface {

    @Multipart
    @POST("send_sms")
    fun uploadDataWithImage(
        @Part file: MultipartBody.Part,
        @Part("slot") slot: RequestBody,
        @Part("date") date: RequestBody,
        @Part("doctor_name") doctor_name: RequestBody
    ): Call<ImageWithDataUploadResponse>

    @GET("history")
    fun getDateHistory(): Call<List<String>>

    @GET("history/{date}")
    fun getSlotHistory(@Path("date") date: String): Call<List<String>>

    @GET("history/{date}/{slot}")
    fun getPatientStatusHistory(
        @Path("date") date: String,
        @Path("slot") slot: String
    ): Call<List<HistoryStatus>>

    @FormUrlEncoded
    @POST("update_number")
    fun updateNumber(
        @Field("number") number: String,
        @Field("patient_id") patientId: String
    ): Call<Message>
}
