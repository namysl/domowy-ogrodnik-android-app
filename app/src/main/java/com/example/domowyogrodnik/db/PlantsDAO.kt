package com.example.domowyogrodnik.db

import androidx.room.*

@Dao
interface PlantsDAO {
    //show all items in default manner
    @get:Query("SELECT * FROM PlantsDB")
    val all: List<PlantsDB?>?

    //show all items in descending order by id, so new items first
    @get:Query("SELECT * FROM PlantsDB ORDER BY id DESC ")
    val allDesc: List<PlantsDB?>?

    @Insert
    fun insert(plantsDB: PlantsDB?)

    @Delete
    fun delete(plantsDB: PlantsDB?)

    @Update
    fun update(plantsDB: PlantsDB?)
}