package com.nuzchpt.scrumpoker

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        initializeKoin {
            androidContext(this@MyApplication)
        }
    }
}