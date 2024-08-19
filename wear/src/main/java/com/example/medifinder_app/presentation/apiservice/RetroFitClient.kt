package com.example.medifinder_app.presentation.apiservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    // Si se desea apuntar hacia localhost debe utilizarse la dirección IP que se presenta a continuación
    // Esto porque al intentar apuntar hacia 127.0.0.1 se haría referencia al teléfono virtual.
    //private const val BASE_URL = "http://10.0.2.2:8080/api/"
    private const val BASE_URL = "http://192.168.100.32:5257/api/"
    val instance: AuthApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(AuthApiService::class.java)
    }
}