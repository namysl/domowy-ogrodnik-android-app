package com.example.domowyogrodnik

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.domowyogrodnik.models_adapters.PlantModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


class AddReminderActivity: Serializable, AppCompatActivity() {  //(val db_object: PlantsDB)
    private var buttonSave: Button? = null
    private var imageViewPic: ImageView? = null
    private var textViewName: TextView? = null
    private var textViewDescription: TextView? = null
    private var inputTime: TextView? = null
    private var inputDate: TextView? = null
    private var editTextFrequency: EditText? = null
    private var spinner: Spinner? = null
    private var spinnerDropDown: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addreminder)

        val intent = intent
        val plant = intent.getSerializableExtra("plant_reminder") as PlantModel?

        buttonSave = findViewById(R.id.button_save)
        imageViewPic = findViewById(R.id.imageView_pic)
        textViewName = findViewById(R.id.TextView_name)
        textViewDescription = findViewById(R.id.TextView_description)
        inputDate = findViewById(R.id.editText_date)
        inputTime = findViewById(R.id.editText_time)
        editTextFrequency = findViewById(R.id.editText_frequency)
        spinner = findViewById(R.id.spinner)
        spinnerDropDown = findViewById(R.id.imageView_dropdown)

        loadImageFromStorage(plant?.photo, imageViewPic)
        textViewName?.text = plant?.name

        if(plant?.description!!.isNotEmpty()){
            textViewDescription?.text = plant.description
            textViewDescription?.movementMethod = ScrollingMovementMethod()
        }
        else{
            textViewDescription?.visibility = View.INVISIBLE
        }

        ArrayAdapter.createFromResource(this, R.array.chores, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner?.adapter = adapter
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
            val timerPiker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

            timerPiker.addOnPositiveButtonClickListener{
                val minute = if (timerPiker.minute in 0..9) "0${timerPiker.minute}" else timerPiker.minute
                val hour = if (timerPiker.hour in 0..9) "0${timerPiker.hour}" else timerPiker.hour

                inputTime?.text = "${hour}:${minute}"
            }
            timerPiker.show(supportFragmentManager, null)
        }

        buttonSave?.setOnClickListener{
            if(inputDate?.text!!.isEmpty() || inputTime?.text!!.isEmpty()){
                Toast.makeText(this, "Dodaj datę i godzinę", Toast.LENGTH_SHORT).show()
            }
            else if(editTextFrequency?.text!!.isEmpty()){
                editTextFrequency?.error = "Wymagane pole"
            }
            else{
                println("elo " + inputDate?.text + " " + inputTime?.text)
                Toast.makeText(this, "Zapisano przypomnienie", Toast.LENGTH_SHORT).show()
                //TODO db + work manager
            }
        }

        val animation = AnimationUtils.loadAnimation(this, R.anim.turn)
        buttonSave?.startAnimation(animation)
        imageViewPic?.startAnimation(animation)
        textViewName?.startAnimation(animation)
        textViewDescription?.startAnimation(animation)
        inputDate?.startAnimation(animation)
        inputTime?.startAnimation(animation)
        editTextFrequency?.startAnimation(animation)
        spinner?.startAnimation(animation)
        spinnerDropDown?.startAnimation(animation)
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