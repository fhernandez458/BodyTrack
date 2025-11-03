package com.fhzapps.bodytrack

import android.app.Application
import com.fhzapps.bodytrack.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BodyTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BodyTrackApplication)
            modules(appModules)
        }
    }
}