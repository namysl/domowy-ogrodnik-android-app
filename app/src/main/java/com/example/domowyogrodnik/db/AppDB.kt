package com.example.domowyogrodnik.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.domowyogrodnik.db.plants_table.PlantsDAO
import com.example.domowyogrodnik.db.plants_table.PlantsDB
import com.example.domowyogrodnik.db.reminders_table.RemindersDAO
import com.example.domowyogrodnik.db.reminders_table.RemindersDB

@Database(entities = [PlantsDB::class, RemindersDB::class], version = 3)
abstract class AppDB : RoomDatabase() {
    abstract fun plantsDAO(): PlantsDAO?
    abstract fun remindersDAO(): RemindersDAO?
}