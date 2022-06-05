package com.example.domowyogrodnik

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.reminders_table.RemindersDB
import com.example.domowyogrodnik.models_adapters.PlantModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


class AddReminderActivity: Serializable, AppCompatActivity() {  //(val db_object: PlantsDB)
    private var buttonSave: Button? = null
    private var imageViewPic: ImageView? = null
    private var tvName: TextView? = null
    private var tvDescription: TextView? = null
    private var inputTime: TextView? = null
    private var inputDate: TextView? = null
    private var spinnerFrequency: Spinner? = null
    private var spinnerDropDown1: ImageView? = null
    private var spinnerChore: Spinner? = null
    private var spinnerDropDown2: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addreminder)

        val plant = intent.getSerializableExtra("plant_reminder") as PlantModel?

        buttonSave = findViewById(R.id.button_save)
        imageViewPic = findViewById(R.id.imageView_pic)
        tvName = findViewById(R.id.tv_name)
        tvDescription = findViewById(R.id.tv_description)
        inputDate = findViewById(R.id.editText_date)
        inputTime = findViewById(R.id.editText_time)
        spinnerFrequency = findViewById(R.id.spinner_frequency_dwmy)
        spinnerDropDown1 = findViewById(R.id.imageView_dropdown1)
        spinnerChore = findViewById(R.id.spinner)
        spinnerDropDown2 = findViewById(R.id.imageView_dropdown2)

        loadImageFromStorage(plant?.photo, imageViewPic)
        tvName?.text = plant?.name

        // if not empty display description text field and make it scrollable
        if(plant?.description!!.isNotEmpty()){
            tvDescription?.text = plant.description
            tvDescription?.movementMethod = ScrollingMovementMethod()
        }
        else{
            tvDescription?.visibility = View.INVISIBLE
        }

        // frequency spinner (cyclic notifications)
        ArrayAdapter.createFromResource(this, R.array.dwmy, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFrequency?.adapter = adapter
            }

        // chore spinner
        ArrayAdapter.createFromResource(this, R.array.chores, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerChore?.adapter = adapter
            }

        // date picker
        inputDate?.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().build()

            datePicker.addOnPositiveButtonClickListener{
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                inputDate?.text = (formatDate.format(Date(it + offset)))
            }
            datePicker.show(supportFragmentManager, "date_picker")
        }

        // time picker
        inputTime?.setOnClickListener{
            val timerPicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

            timerPicker.addOnPositiveButtonClickListener{
                val hour = if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour
                val minute = if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute

                inputTime?.text = "${hour}:${minute}"
            }
            timerPicker.show(supportFragmentManager, null)
        }

        buttonSave?.setOnClickListener{
            if(inputDate?.text!!.isEmpty() || inputTime?.text!!.isEmpty()){
                Toast.makeText(this, "Dodaj datę i godzinę", Toast.LENGTH_SHORT).show()
            }
            else{
                println("LOGCAT date time" + inputDate?.text + " " + inputTime?.text)

                val newReminder = RemindersDB()
                newReminder.date = inputDate?.text.toString()
                newReminder.time = inputTime?.text.toString()
                newReminder.chore = spinnerChore?.selectedItem.toString()
                newReminder.frequency = spinnerFrequency?.selectedItem.toString()
                newReminder.plantName = plant.name
                newReminder.plantPhoto = plant.photo

                if(newReminder.frequency == "powtarzaj codziennie" && newReminder.chore == "podlewanie") {
                    val alertDialog = AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_warning)
                        .setTitle("Czy utworzyć przypomnienie?")
                        .setMessage("Codzienne podlewanie może zaszkodzić Twoim roślinom! " +
                                "Czy na pewno chcesz dodać to przypomnienie?")

                    alertDialog.setPositiveButton("Tak") { _, _ ->
                        addAlarmAndFinish(newReminder)
                    }

                    alertDialog.setNegativeButton("Anuluj") { _, _ -> }

                    alertDialog.show()
                }
                else{
                    addAlarmAndFinish(newReminder)
                }
            }
        }
        animateView(plant)
    }

    private fun addAlarmAndFinish(newReminder: RemindersDB){
        CoroutineScope(Dispatchers.IO).launch {
            val reminderID: Long? =
                ClientDB.getInstance(applicationContext)?.appDatabase?.remindersDAO()
                    ?.insert(newReminder)

            val array = arrayOf(newReminder.plantPhoto,
                                newReminder.plantName,
                                spinnerChore?.selectedItem.toString())

            val formatDateTime = SimpleDateFormat("dd/MM/yyyyHH:mm", Locale.getDefault())
            val formatted = formatDateTime.parse(newReminder.date + newReminder.time)

            val calendar = Calendar.getInstance()
            calendar.time = formatted!!

            startAlarm(calendar, array, reminderID!!)
            println("LOGCAT reminderID $reminderID")
        }
        Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show()
        this.finish() // closes fragment
        startActivity(Intent(this, MainActivity::class.java)) // moves to homepage
    }

    private fun startAlarm(calendar: Calendar, plantInfo: Array<String?>, requestID: Long){
        // prepares the alarm manager
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        intent.putExtra("plantPhoto", plantInfo[0])
        intent.putExtra("plantName", plantInfo[1])
        intent.putExtra("plantChore", plantInfo[2])

        // requestCode should be unique to allow multiple notifications,
        // so we take autogenerated key id from db
        val pendingIntent = PendingIntent.getBroadcast(this, requestID.toInt(),
                                                       intent, FLAG_UPDATE_CURRENT)

        if(spinnerFrequency?.selectedItem.toString() == "powtarzanie wyłączone"){
            // exact -> does not permit the OS to adjust the delivery time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
        else{
            // inexact -> the OS will shift alarms in order to minimize wakeups and battery use
            var repeat = AlarmManager.INTERVAL_DAY  // daily
            when (spinnerFrequency?.selectedItem.toString()){
                "powtarzaj co tydzień" -> repeat *= 7
                "powtarzaj co dwa tygodnie" -> repeat *= 14
                "powtarzaj co miesiąc" -> repeat *= 30
                "powtarzaj co trzy miesiące" -> repeat *= 90
            }
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, repeat, pendingIntent)
        }
    }

    private fun animateView(plant: PlantModel){
        val animation = AnimationUtils.loadAnimation(this, R.anim.turn)
        buttonSave?.startAnimation(animation)
        imageViewPic?.startAnimation(animation)
        tvName?.startAnimation(animation)
        if(plant.description!!.isNotEmpty()){
            tvDescription?.startAnimation(animation)
        }
        inputDate?.startAnimation(animation)
        inputTime?.startAnimation(animation)
        spinnerFrequency?.startAnimation(animation)
        spinnerDropDown1?.startAnimation(animation)
        spinnerChore?.startAnimation(animation)
        spinnerDropDown2?.startAnimation(animation)
    }

    private fun loadImageFromStorage(path: String?, img: ImageView?){
        try{
            val f = File(path, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            img!!.setImageBitmap(b)
        }
        catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }
}