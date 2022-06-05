package com.example.domowyogrodnik.models_adapters

import com.example.domowyogrodnik.db.reminders_table.RemindersDB
import java.io.Serializable

class ReminderModel(val date: String?, val time: String?, val chore: String?, val frequency: String?,
                    val plantName: String?, val plantPhoto: String, val db_object: RemindersDB) : Serializable