package com.example.domowyogrodnik.db.plants_table

import androidx.room.*

@Dao
interface PlantsDAO {
    //show all items in ascending order, so new items last
    @Query("SELECT * FROM PlantsDB")
    fun allAsc(): List<PlantsDB>

    //show all items in descending order by id, so new items first
    @Query("SELECT * FROM PlantsDB ORDER BY id DESC ")
    fun allDesc(): List<PlantsDB>

    @Insert
    fun insert(plantsDB: PlantsDB)

    @Delete
    fun delete(plantsDB: PlantsDB)

    @Update
    fun update(plantsDB: PlantsDB)
}