package com.example.domowyogrodnik.fragments

import android.animation.ValueAnimator
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.R
import com.example.domowyogrodnik.db.ClientDB
import com.example.domowyogrodnik.db.plants_table.PlantsDB


class HomeFragment : Fragment() {
    private var textViewHello: TextView? = null
    private var textViewStats: TextView? = null
    private var logo: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        textViewHello = rootView.findViewById(R.id.text_home)
        textViewStats = rootView.findViewById(R.id.text_stats)
        logo = rootView.findViewById(R.id.imageView_logo)

        class LoadFromDB : AsyncTask<Void?, Void?, List<PlantsDB>?>(){
            override fun doInBackground(vararg p0: Void?): List<PlantsDB>?{
                //retrieve data from DB
                return ClientDB.getInstance(requireContext())?.appDatabase?.plantsDAO()?.allDesc()
            }

            override fun onPostExecute(db: List<PlantsDB>?){
                super.onPostExecute(db)

                if (db != null) {
                    textViewHello?.text = getString(R.string.stats_notempty)
                    startCountAnimation(db.size)
                }
                else {
                    textViewStats?.text = getString(R.string.stats_empty)
                }
            }
        }
        LoadFromDB().execute()

        val animation = AnimationUtils.loadAnimation(rootView.context, R.anim.turn)
        textViewHello?.startAnimation(animation)
        textViewStats?.startAnimation(animation)
        logo?.startAnimation(animation)

        return rootView
    }

    private fun startCountAnimation(max: Int) {
        val animator = ValueAnimator.ofInt(0, max)
        animator.duration = 1000 //in milliseconds
        animator.addUpdateListener{ animation ->
            val stats: CharSequence = "${animation.animatedValue}"
            textViewStats?.text = stats
        }
        animator.start()
    }
}
