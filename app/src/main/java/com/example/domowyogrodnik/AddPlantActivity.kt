package com.example.domowyogrodnik

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.PlantsDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.security.SecureRandom
import java.util.ArrayList

class AddPlantActivity : AppCompatActivity(){
    private var buttonPhoto: Button? = null
    private var buttonGallery: Button? = null
    private var buttonSave: Button? = null
    private var imageView: ImageView? = null
    private var editTextName: EditText? = null
    private var editTextDescription: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addplant)

        buttonPhoto = findViewById(R.id.button_photo)
        buttonGallery = findViewById(R.id.button_gallery)
        buttonSave = findViewById(R.id.button_save)
        imageView = findViewById(R.id.imageView)
        editTextName = findViewById(R.id.editText_name)
        editTextDescription = findViewById(R.id.editText_description)

        if (checkAndRequestPermissions(this@AddPlantActivity)){
            buttonPhoto?.setOnClickListener{
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
                //TODO sprawdzic co z ta kompresja
            }

            buttonGallery?.setOnClickListener{
                val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)
            }
        }

        buttonSave?.setOnClickListener{ view ->
            try{
                val picture = saveToInternalStorage((imageView!!.drawable as BitmapDrawable).bitmap)

                if (isEmpty(editTextName?.text.toString())){
                    editTextName?.error = "Wymagane pole"
                }
                else {
                    val newPlant = PlantsDB()
                    newPlant.path = picture
                    newPlant.name = editTextName!!.text.toString()
                    newPlant.description = editTextDescription!!.text.toString()

                    CoroutineScope(IO).launch {
                        ClientDB.getInstance(applicationContext)?.appDatabase?.plantsDAO()?.insert(newPlant)
                    }

                    Toast.makeText(view.context, "Zapisano", Toast.LENGTH_SHORT).show()

                    //TODO żeby odświeżało może galerię, bo do home już umiem wywalać
                    this.finish() //closes fragment
                    startActivity(Intent(this, MainActivity::class.java)) //moves to homepage
                    //this.onBackPressed() //back to gallery
                }
            } catch (e: Exception){
                Toast.makeText(view.context, "Dodaj zdjęcie", Toast.LENGTH_LONG).show()
            }
        }

        val animation = AnimationUtils.loadAnimation(this, R.anim.turn)
        buttonPhoto?.startAnimation(animation)
        buttonGallery?.startAnimation(animation)
        buttonSave?.startAnimation(animation)
        editTextName?.startAnimation(animation)
        editTextDescription?.startAnimation(animation)
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
                //camera
                0 -> if (resultCode == RESULT_OK && data != null){
                        val selectedImage = data.extras!!["data"] as Bitmap?
                        imageView!!.setImageBitmap(selectedImage)
                }
                //gallery
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
        const val RESULT_CANCELED = 0
        const val RESULT_OK = -1
        const val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        var rnd = SecureRandom()

        //function to check permission
        fun checkAndRequestPermissions(context: Activity?): Boolean{
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

    private fun randomString(len: Int): String{
        val sb = StringBuilder(len)
        for (i in 0 until len) sb.append(AB[rnd.nextInt(AB.length)])
        return sb.toString()
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String{
        val cw = ContextWrapper(this@AddPlantActivity)

        //filename as a random alphanumeric string
        val directory = cw.getDir(randomString(20), Context.MODE_PRIVATE)
        //create file
        val mypath = File(directory, "profile.jpg")
        var fos: FileOutputStream? = null
        try{
            fos = FileOutputStream(mypath)
            //use the compress method on the bitmap object to write image to the outputstream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        finally{
            try {
                assert(fos != null)
                fos!!.close()
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }
}