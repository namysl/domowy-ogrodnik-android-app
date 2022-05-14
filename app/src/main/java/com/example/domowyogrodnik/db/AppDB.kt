package com.example.domowyogrodnik.db

import androidx.room.Database
import androidx.room.RoomDatabase

//@Database(entities = [WardrobeDB::class, SavedCollectionsDB::class], version = 2)
@Database(entities = [PlantsDB::class], version = 2)
abstract class AppDB : RoomDatabase() {
    abstract fun plantsDAO(): PlantsDAO?
}