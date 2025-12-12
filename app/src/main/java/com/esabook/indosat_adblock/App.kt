package com.esabook.indosat_adblock

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class App : Application() {
    companion object {
        lateinit var db: AppDatabase
            private set

        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()

        db = AppDatabase.getDatabase(applicationContext)
        context = this

        FirebaseApp.initializeApp(this)
    }
}