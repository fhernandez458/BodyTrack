package com.fhzapps.bodytrack.di

import android.os.Build
import android.util.Log
import com.fhzapps.bodytrack.networking.ExerciseApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


fun provideHttpClient(): OkHttpClient {
    return OkHttpClient
        .Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
           APIKeyInterceptor().intercept(chain)
        }
        .build()
}

fun provideConverterFactory(): GsonConverterFactory =
    GsonConverterFactory.create()

fun provideRetrofit(
    okHttpClient: OkHttpClient, gsonConverterFactory:
    GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://exercisedb-api1.p.rapidapi.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideService(retrofit: Retrofit) : ExerciseApi =
    retrofit.create(ExerciseApi::class.java)

class APIKeyInterceptor : Interceptor {
    val apiKey = "f331d51c0amsh341ca9b8a82ab86p1906f6jsn8817dd9102ee"
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()
        currentRequest.addHeader("x-rapidapi-host", "exercisedb-api1.p.rapidapi.com")
        currentRequest.addHeader("x-rapidapi-key", apiKey)

        val newRequest = currentRequest.build()
        Log.d("NetworkModule", "Request URL: ${newRequest.url}") // Log the full URL to debug
        Log.d("NetworkModule", "Request Headers: ${newRequest.headers}")
        return chain.proceed(newRequest)
    }
}

val networkModule = module {
    single{ provideHttpClient() }
    single { provideConverterFactory() }
    single { provideRetrofit(get(), get() )}
    single { provideService( get()) }
}