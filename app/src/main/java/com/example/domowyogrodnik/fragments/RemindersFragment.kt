package com.example.domowyogrodnik.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.R
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.reminders_table.RemindersDB
import com.example.domowyogrodnik.models_adapters.ReminderAdapter
import com.example.domowyogrodnik.models_adapters.ReminderModel

class RemindersFragment : Fragment() {
    lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val rootView = inflater.inflate(R.layout.fragment_reminders, container, false)

        listView = rootView.findViewById(R.id.listView)
        val list = mutableListOf<ReminderModel>()

        class LoadFromDB : AsyncTask<Void?, Void?, List<RemindersDB>?>(){
            override fun doInBackground(vararg p0: Void?): List<RemindersDB>?{
                //retrieve data from DB
                return ClientDB.getInstance(requireContext())?.appDatabase?.remindersDAO()?.allDesc()
            }

            override fun onPostExecute(db: List<RemindersDB>?){
                super.onPostExecute(db)

                if (db != null) {
                    for (element in db){
                        list.add(ReminderModel(element.date, element.time, element.chore, element.plantName, element.plantPhoto!!, element))
                    }

                    listView.adapter = ReminderAdapter(requireContext(), R.layout.single_item_reminder, list)
                }
            }
        }

        LoadFromDB().execute()

        val animation = AnimationUtils.loadAnimation(rootView.context, R.anim.turn)
        listView.startAnimation(animation)

        return rootView
    }
}
