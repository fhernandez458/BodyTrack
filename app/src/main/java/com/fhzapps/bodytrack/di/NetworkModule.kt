package com.fhzapps.bodytrack.di

import com.fhzapps.bodytrack.networking.ExerciseApi
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


fun provideHttpClient(): OkHttpClient {
    return OkHttpClient
        .Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}

fun provideConverterFactory(): GsonConverterFactory =
    GsonConverterFactory.create()

fun provideRetrofit(
    okHttpClient: OkHttpClient, gsonConverterFactory:
    GsonConverterFactory
): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://exerciserepov1-7q0ggxa9q-fhernandez458s-projects.vercel.app/api/v1/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideService(retrofit: Retrofit) : ExerciseApi =
    retrofit.create(ExerciseApi::class.java)

val networkModule = module {
    single{ provideHttpClient() }
    single { provideConverterFactory() }
    single { provideRetrofit(get(), get() )}
    single { provideService( get()) }
}