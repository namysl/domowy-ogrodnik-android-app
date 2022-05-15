package com.example.domowyogrodnik

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class MyListAdapter(var mCtx: Context, var resource: Int, var items:List<Model>): ArrayAdapter<Model>(mCtx, resource, items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
        val layoutInflater :LayoutInflater = LayoutInflater.from(mCtx)
        val view : View = layoutInflater.inflate(resource , null )
        val imageView :ImageView = view.findViewById(R.id.iconIv)
        var textView : TextView = view.findViewById(R.id.titleTv)
        var textView1 : TextView = view.findViewById(R.id.descTv)

        var person : Model = items[position]

//        imageView.setImageDrawable(mCtx.resources.getDrawable(person.photo))
        loadImageFromStorage(person.photo, imageView)
        textView.text = person.title
        textView1.text = person.desc

        return view
    }

    private fun loadImageFromStorage(path: String, img: ImageView?) {
        try {
            val f = File(path, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            img!!.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

}