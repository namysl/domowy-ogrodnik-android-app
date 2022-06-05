package com.example.domowyogrodnik.models_adapters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.domowyogrodnik.AlarmReceiver
import com.example.domowyogrodnik.R
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.reminders_table.RemindersDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class ReminderAdapter(private var current_context: Context, private var resource: Int, private var items:List<ReminderModel>)
    : ArrayAdapter<ReminderModel>(current_context, resource, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(current_context)
        val view: View = layoutInflater.inflate(resource, null)
        val reminder: ReminderModel = items[position]

        val imageViewPlantPhoto: ImageView = view.findViewById(R.id.imageView_plantphoto)
        val textViewChore: TextView = view.findViewById(R.id.textView_chore)
        val textViewPlantName: TextView = view.findViewById(R.id.textView_plantname)
        val textViewDate: TextView = view.findViewById(R.id.textView_date)
        val textViewTime: TextView = view.findViewById(R.id.textView_time)
        val buttonDelete: Button = view.findViewById(R.id.button_delete)

        loadImageFromStorage(reminder.plantPhoto, imageViewPlantPhoto)
        textViewChore.text = reminder.chore
        textViewPlantName.text = reminder.plantName
        textViewDate.text = reminder.date
        textViewTime.text = reminder.time

        buttonDelete.setOnClickListener{
            deleteEntryInDB(reminder.db_object)
            buttonDelete.visibility = View.GONE

            imageViewPlantPhoto.setImageResource(R.drawable.ic_reminders)
            textViewChore.text = context.getString(R.string.deleted)
            textViewPlantName.text = ""
            textViewDate.text = ""
            textViewTime.text = ""
            buttonDelete.visibility = View.GONE

            // cancel the alarm
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, reminder.db_object.id,
                                                           intent, PendingIntent.FLAG_UPDATE_CURRENT)
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }

        return view
    }

    private fun loadImageFromStorage(path: String, img: ImageView?){
        try{
            val f = File(path, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            img!!.setImageBitmap(b)
        }
        catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }

    private fun deleteEntryInDB(task: RemindersDB){
        CoroutineScope(Dispatchers.IO).launch{
            ClientDB.getInstance(context)?.appDatabase?.remindersDAO()?.delete(task)
        }
    }
}