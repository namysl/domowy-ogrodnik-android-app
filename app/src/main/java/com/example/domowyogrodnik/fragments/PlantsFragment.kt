package com.example.domowyogrodnik.fragments


import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.AddPlantActivity
import com.example.domowyogrodnik.models_adapters.PlantModel
import com.example.domowyogrodnik.models_adapters.PlantAdapter
import com.example.domowyogrodnik.R
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.plants_table.PlantsDB

class PlantsFragment : Fragment(){
    private var buttonAddNew: Button? = null
    lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val rootView = inflater.inflate(R.layout.fragment_plants, container, false)

        buttonAddNew = rootView.findViewById(R.id.button)

        buttonAddNew?.setOnClickListener{
            val intent = Intent (activity, AddPlantActivity::class.java)
            activity?.startActivity(intent)
        }

        listView = rootView.findViewById(R.id.listView)
        val list = mutableListOf<PlantModel>()

        class LoadFromDB : AsyncTask<Void?, Void?, List<PlantsDB>?>(){
            override fun doInBackground(vararg p0: Void?): List<PlantsDB>?{
                //retrieve data from DB
                return ClientDB.getInstance(requireContext())?.appDatabase?.plantsDAO()?.allDesc()
            }

            override fun onPostExecute(db: List<PlantsDB>?){
                super.onPostExecute(db)

                if (db != null) {
                    for (element in db){
                        list.add(PlantModel(element.name, element.description, element.path!!, element))
                    }

                    listView.adapter = PlantAdapter(requireContext(), R.layout.single_item_plant, list)
                }
            }
        }

        LoadFromDB().execute()

        val animation = AnimationUtils.loadAnimation(rootView.context, R.anim.turn)
        buttonAddNew?.startAnimation(animation)
        listView.startAnimation(animation)

        return rootView
    }
}
