package com.example.domowyogrodnik.ui.list_plants

import android.app.AlertDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.AddPlantActivity
import com.example.domowyogrodnik.Model
import com.example.domowyogrodnik.MyListAdapter
import com.example.domowyogrodnik.R
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.PlantsDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class PlantsFragment : Fragment() {
    var button: Button? = null
    lateinit var listView : ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_plants, container, false)

        button = rootView.findViewById(R.id.button)

        button?.setOnClickListener { view ->
            val intent = Intent (activity, AddPlantActivity::class.java)
            activity?.startActivity(intent)
        }

        listView = rootView.findViewById(R.id.listView)
        var list = mutableListOf<Model>()

//        CoroutineScope(IO).launch{
//            //dane z bazy
//            var db = ClientDB.getInstance(requireContext())?.appDatabase?.plantsDAO()?.allDesc()
//            println("# $db")
//            println("# " + db?.size)
//            println("# " + db?.get(0)?.path + " " + db?.get(0)?.name)
//
//            //w wątku lista też :(
//            if (db != null) {
//                for (element in db)
//                    list.add(Model(element.name, "opisik", R.drawable.gradient_textview))
//
//            }
//        }

        class LoadFromDB : AsyncTask<Void?, Void?, List<PlantsDB>?>() {
            protected override fun doInBackground(vararg p0: Void?): List<PlantsDB>? {
                //retrieve data from DB
                var db = ClientDB.getInstance(requireContext())?.appDatabase?.plantsDAO()?.allDesc()
                println("# DB: $db")
                return db
            }

            override fun onPostExecute(db: List<PlantsDB>?) {
                super.onPostExecute(db)

                if (db != null) {
                    for (element in db)
                        list.add(Model(element.name, "opisik", element.path!!))

                }

//                list.add(Model("A", "Description A...", R.drawable.gradient_buttons))
//                list.add(Model("B", "Description B...", R.drawable.gradient_list))
//                list.add(Model("C", "Description C...", R.drawable.gradient_navigation))
//                list.add(Model("D", "Description D...", R.drawable.gradient_textview))

                listView.adapter = MyListAdapter(requireContext(), R.layout.single_item, list)

                listView.setOnItemClickListener { parent, view, position, id ->

                    if (position == 0) {
                        Toast.makeText(context, "This is A", Toast.LENGTH_SHORT).show()

                        val alertadd = AlertDialog.Builder(context)
                        val factory = LayoutInflater.from(context)
                        val view: View = factory.inflate(R.layout.picture_popup, null)

                        // change the ImageView image source
                        val dialogImageView: ImageView =
                            view.findViewById<View>(R.id.dialog_imageview) as ImageView
                        dialogImageView.setImageResource(R.drawable.ic_plants)

                        alertadd.setView(view)
                        alertadd.setTitle(":)")
                        alertadd.setNeutralButton("OK") { dlg, sumthin -> }

                        alertadd.show()

                    }
                    if (position == 1) {
                        Toast.makeText(context, "This is Java", Toast.LENGTH_SHORT).show()
                    }
                    if (position == 2) {
                        Toast.makeText(context, "This is cplusplus", Toast.LENGTH_SHORT).show()
                    }
                    if (position == 3) {
                        Toast.makeText(context, "This is Kotlin", Toast.LENGTH_SHORT).show()
                    }
                    if (position == 4) {
                        Toast.makeText(context, "This is Flutter", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        LoadFromDB().execute()

        return rootView
    }
}
