package com.example.roomdatabaseapp

import android.app.Application
import com.example.roomdatabaseapp.db.EmployeeDatabase

class EmployeeApp:Application() {
    val db by lazy {
        EmployeeDatabase.getInstance(this)
    }
}