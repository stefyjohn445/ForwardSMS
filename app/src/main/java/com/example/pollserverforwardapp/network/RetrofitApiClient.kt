package com.example.pollserverforwardapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitApiClient {

    companion object{
        fun getUpload(): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
            return Retrofit.Builder()
//                .baseUrl("http://10.10.3.66:8000/")
                .baseUrl("http://3.109.144.72:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }
}