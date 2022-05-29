package com.example.domowyogrodnik

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
    private var editTextFrequency: EditText? = null
    private var spinnerFrequency: Spinner? = null
    private var spinnerDropDown1: ImageView? = null
    private var spinnerChore: Spinner? = null
    private var spinnerDropDown2: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addreminder)

        val intent = intent
        val plant = intent.getSerializableExtra("plant_reminder") as PlantModel?

        buttonSave = findViewById(R.id.button_save)
        imageViewPic = findViewById(R.id.imageView_pic)
        tvName = findViewById(R.id.tv_name)
        tvDescription = findViewById(R.id.tv_description)
        inputDate = findViewById(R.id.editText_date)
        inputTime = findViewById(R.id.editText_time)
        editTextFrequency = findViewById(R.id.editText_frequency_num)
        spinnerFrequency = findViewById(R.id.spinner_frequency_dwmy)
        spinnerDropDown1 = findViewById(R.id.imageView_dropdown1)
        spinnerChore = findViewById(R.id.spinner)
        spinnerDropDown2 = findViewById(R.id.imageView_dropdown2)

        loadImageFromStorage(plant?.photo, imageViewPic)
        tvName?.text = plant?.name

        if(plant?.description!!.isNotEmpty()){
            tvDescription?.text = plant.description
            tvDescription?.movementMethod = ScrollingMovementMethod()
        }
        else{
            tvDescription?.visibility = View.INVISIBLE
        }

        ArrayAdapter.createFromResource(this, R.array.dwmy, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerFrequency?.adapter = adapter
            }

        ArrayAdapter.createFromResource(this, R.array.chores, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerChore?.adapter = adapter
            }

        inputDate?.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().build()

            datePicker.addOnPositiveButtonClickListener{
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                inputDate?.text = (formatDate.format(Date(it + offset)))
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        inputTime?.setOnClickListener{
            val timerPicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

            timerPicker.addOnPositiveButtonClickListener{
                val minute = if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute
                val hour = if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour

                inputTime?.text = "${hour}:${minute}"
            }
            timerPicker.show(supportFragmentManager, null)
        }

        buttonSave?.setOnClickListener{
            if(inputDate?.text!!.isEmpty() || inputTime?.text!!.isEmpty()){
                Toast.makeText(this, "Dodaj datę i godzinę", Toast.LENGTH_SHORT).show()
            }
            else{
                println("elo " + inputDate?.text + " " + inputTime?.text)

                val newReminder = RemindersDB()
                newReminder.date = inputDate?.text.toString()
                newReminder.time = inputTime?.text.toString()
                newReminder.chore = spinnerChore?.selectedItem.toString()
                newReminder.plantName = plant.name
                newReminder.plantPhoto = plant.photo

                CoroutineScope(Dispatchers.IO).launch {
                    ClientDB.getInstance(applicationContext)?.appDatabase?.remindersDAO()?.insert(newReminder)
                }

                //TODO work manager + alarm manager

                Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show()
                this.finish() //closes fragment
                startActivity(Intent(this, MainActivity::class.java)) //moves to homepage
            }
        }

        val animation = AnimationUtils.loadAnimation(this, R.anim.turn)
        buttonSave?.startAnimation(animation)
        imageViewPic?.startAnimation(animation)
        tvName?.startAnimation(animation)
        if(plant.description.isNotEmpty()){
            tvDescription?.startAnimation(animation)
        }
        inputDate?.startAnimation(animation)
        inputTime?.startAnimation(animation)
        editTextFrequency?.startAnimation(animation)
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