package com.example.plana22.RoomDetail

import android.app.Application

class DetailApp : Application() {
    val db  by lazy {
        DetailDatabase.getInstance(this)
    }
}