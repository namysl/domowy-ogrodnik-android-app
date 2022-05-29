package com.example.domowyogrodnik.db.reminders_table

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.io.Serializable

@Entity
class RemindersDB: Serializable {
    //getters & setters
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "time")
    var time: String? = null

    @ColumnInfo(name = "chore")
    var chore: String? = null

    @ColumnInfo(name = "plantName")
    var plantName: String? = null

    @ColumnInfo(name = "plantPhoto")
    var plantPhoto: String? = null
}