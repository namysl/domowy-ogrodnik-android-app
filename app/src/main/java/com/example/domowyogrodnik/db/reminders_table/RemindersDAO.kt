package com.example.domowyogrodnik.db.reminders_table

import androidx.room.*

@Dao
interface RemindersDAO {
    //show all items in ascending order, so new items last
    @Query("SELECT * FROM RemindersDB")
    fun allAsc(): List<RemindersDB>

    //show all items in descending order by id, so new items first
    @Query("SELECT * FROM RemindersDB ORDER BY id DESC ")
    fun allDesc(): List<RemindersDB>

    @Insert
    fun insert(remindersDB: RemindersDB): Long

    @Delete
    fun delete(remindersDB: RemindersDB)

    @Update
    fun update(remindersDB: RemindersDB)
}