package com.example.packlist.activities

import android.app.Application
import com.example.packlist.db.MainDataBase

class MainApp : Application() {
val database by lazy { MainDataBase.getDataBase(this) }
}