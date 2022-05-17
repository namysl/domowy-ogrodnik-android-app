package com.example.domowyogrodnik

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class MyListAdapter(var current_context: Context, var resource: Int, var items:List<Model>): ArrayAdapter<Model>(current_context, resource, items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
        val layoutInflater :LayoutInflater = LayoutInflater.from(current_context)
        val view : View = layoutInflater.inflate(resource , null )
        val plant : Model = items[position]

        val imageView: ImageView = view.findViewById(R.id.imageView_photo)
        val textViewName: TextView = view.findViewById(R.id.textView_name)
        val textViewDescription: TextView = view.findViewById(R.id.textView_description)
        val buttonDelete : Button = view.findViewById(R.id.button_delete)
        val buttonInfo: Button = view.findViewById(R.id.button_info)

        loadImageFromStorage(plant.photo, imageView)
        textViewName.text = plant.name
        textViewDescription.text = plant.description

        buttonDelete.setOnClickListener{
            Toast.makeText(context, "delete ${plant.name}", Toast.LENGTH_SHORT).show()
            //TODO usuwanie z bazy i internal memory
        }

        buttonInfo.setOnClickListener{
            Toast.makeText(context, "powiadomienie ${plant.name}", Toast.LENGTH_SHORT).show()
            //TODO powiadomienia
        }

        imageView.setOnClickListener{
            Toast.makeText(context, "info ${plant.name}", Toast.LENGTH_SHORT).show()

            val alertadd = AlertDialog.Builder(context)
            val factory = LayoutInflater.from(context)
            val dialogView: View = factory.inflate(R.layout.picture_popup, null)

            val dialogImageView: ImageView = dialogView.findViewById(R.id.dialog_imageview)

            loadImageFromStorage(plant.photo, dialogImageView)

            //TODO ładniejsze UI
            alertadd.setView(dialogView)
            alertadd.setIcon(R.drawable.ic_plants)
            alertadd.setTitle(plant.name)
            alertadd.setMessage(plant.description)

            alertadd.setNeutralButton("Wróć") { _, _ -> }

            alertadd.show()
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
}