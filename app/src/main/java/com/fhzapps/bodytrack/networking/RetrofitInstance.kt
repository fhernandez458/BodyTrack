package com.fhzapps.bodytrack.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://exerciserepov1-7q0ggxa9q-fhernandez458s-projects.vercel.app/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}