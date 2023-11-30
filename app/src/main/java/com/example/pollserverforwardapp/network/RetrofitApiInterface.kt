package com.example.pollserverforwardapp.network


import com.example.pollserverforwardapp.models.ImageWithDataUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.Call

interface RetrofitApiInterface {

    @Multipart
    @POST("send_sms")
    fun uploadDataWithImage(
        @Part file: MultipartBody.Part,
        @Part("time") time: RequestBody,
        @Part("date") date: RequestBody,
        @Part("doctor_name") doctor_name: RequestBody
    ): Call<ImageWithDataUploadResponse>
}
