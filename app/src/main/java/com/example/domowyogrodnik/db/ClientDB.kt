package com.example.domowyogrodnik.db

import android.content.Context
import androidx.room.Room

class ClientDB private constructor(private val cntx: Context) {
    val appDatabase: AppDB

    companion object {
        private var clientInstance: ClientDB? = null
        @Synchronized
        fun getInstance(cntx: Context): ClientDB? {
            if (clientInstance == null) {
                clientInstance = ClientDB(cntx)
            }
            return clientInstance
        }
    }

    init {
        appDatabase = Room.databaseBuilder(cntx, AppDB::class.java, "WirtualnaSzafa").build()
    }
}