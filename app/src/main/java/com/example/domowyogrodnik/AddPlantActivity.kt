package com.example.domowyogrodnik

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.ArrayList

class AddPlantActivity : AppCompatActivity() {
    private var buttonPhoto: Button? = null
    private var buttonGallery: Button? = null
    private var buttonSave: Button? = null
    private var imageView: ImageView? = null
    private var editText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addplant)

        buttonPhoto = findViewById(R.id.button_photo)
        buttonGallery = findViewById(R.id.button_gallery)
        buttonSave = findViewById(R.id.button_save)
        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)

        if (checkAndRequestPermissions(this@AddPlantActivity)){
            buttonPhoto?.setOnClickListener{ view ->
                Toast.makeText(view.context, "Zdjęcie!", Toast.LENGTH_SHORT).show()
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            }

            buttonGallery?.setOnClickListener{ view ->
                Toast.makeText(view.context, "Galeria!", Toast.LENGTH_SHORT).show()
                val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)
            }
        }

        buttonSave?.setOnClickListener{ view ->
            if (isEmpty(editText?.text.toString())) {
                Toast.makeText(view.context, "Dodaj nazwę", Toast.LENGTH_SHORT).show()

                //do bazy danych
            }
            else{
//                this.finish() //closes fragment
//                startActivity(Intent(this, MainActivity::class.java)) //moves to homepage
                this.onBackPressed() //back to gallery
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS){
            when {
                ContextCompat.checkSelfPermission(this@AddPlantActivity, Manifest.permission.CAMERA)
                                                  != PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(applicationContext, "Wymagany jest dostęp do kamery", Toast.LENGTH_SHORT).show()
                }
                ContextCompat.checkSelfPermission(this@AddPlantActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                  != PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(applicationContext, "Wymagany jest dostęp do plików w pamięci", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED){
            when (requestCode){
                0 -> if (resultCode == RESULT_OK && data != null){
                        val selectedImage = data.extras!!["data"] as Bitmap?
                        imageView!!.setImageBitmap(selectedImage)
                }
                1 -> if (resultCode == RESULT_OK && data != null){
                        val selectedImage = data.data
                        var bitmap: Bitmap? = null
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                        } catch (e: IOException){
                            e.printStackTrace()
                        }
                        //display picked photo
                        imageView!!.setImageBitmap(bitmap)
                }
            }
        }
    }

    companion object{
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 101

        // function to check permission
        fun checkAndRequestPermissions(context: Activity?): Boolean {
            val wExtstorePermission = ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            val listPermissionsNeeded: MutableList<String> = ArrayList()

            if (cameraPermission != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }
            if (wExtstorePermission != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (listPermissionsNeeded.isNotEmpty()){
                ActivityCompat.requestPermissions(context, listPermissionsNeeded.toTypedArray(),
                                                  REQUEST_ID_MULTIPLE_PERMISSIONS)
                return false
            }
            return true
        }
    }
}