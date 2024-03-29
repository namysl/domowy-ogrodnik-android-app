package com.example.domowyogrodnik.models_adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.domowyogrodnik.AddReminderActivity
import com.example.domowyogrodnik.R
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.plants_table.PlantsDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class PlantAdapter(private var current_context: Context, private var resource: Int, private var items:List<PlantModel>)
    : ArrayAdapter<PlantModel>(current_context, resource, items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
        val layoutInflater: LayoutInflater = LayoutInflater.from(current_context)
        val view: View = layoutInflater.inflate(resource , null )
        val plant: PlantModel = items[position]

        val imageViewPhoto: ImageView = view.findViewById(R.id.imageView_plantphoto)
        val textViewName: TextView = view.findViewById(R.id.textView_chore)
        val textViewDescription: TextView = view.findViewById(R.id.textView_description)
        val buttonDelete: Button = view.findViewById(R.id.button_delete)
        val buttonAddReminder: Button = view.findViewById(R.id.button_addreminder)

        loadImageFromStorage(plant.photo, imageViewPhoto)
        textViewName.text = plant.name
        textViewDescription.text = plant.description

        buttonDelete.setOnClickListener{
            deleteEntryInDB(plant.db_object)
            deleteImageFromInternalStorage(plant.photo)

            imageViewPhoto.setImageResource(R.drawable.ic_plants)
            textViewName.text = context.getString(R.string.deleted)
            textViewDescription.text = ""

            buttonDelete.visibility = View.GONE
            buttonAddReminder.visibility = View.GONE

            imageViewPhoto.setOnClickListener(null)

            val icon: ImageView = view.findViewById(R.id.imageView_zoom)
            icon.visibility = View.GONE
        }

        buttonAddReminder.setOnClickListener{
            val intent = Intent(context, AddReminderActivity::class.java)
            intent.putExtra("plant_reminder", plant)
            context.startActivities(arrayOf(intent))
        }

        imageViewPhoto.setOnClickListener{
            val info = AlertDialog.Builder(context)
            val factory = LayoutInflater.from(context)
            val dialogView: View = factory.inflate(R.layout.picture_popup, null)

            val infoImageView: ImageView = dialogView.findViewById(R.id.dialog_imageview)

            loadImageFromStorage(plant.photo, infoImageView)

            info.setView(dialogView)
            info.setIcon(R.drawable.ic_plants)
            info.setTitle(plant.name)
            //info.setMessage(plant.description)
            info.setNeutralButton("Wróć") { _, _ -> }
            info.show()
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

    private fun deleteEntryInDB(task: PlantsDB){
        CoroutineScope(Dispatchers.IO).launch{
            ClientDB.getInstance(context)?.appDatabase?.plantsDAO()?.delete(task)
        }
    }

    private fun deleteImageFromInternalStorage(path: String): Boolean {
        val f = File(path, "profile.jpg")
        return f.delete()
    }
}