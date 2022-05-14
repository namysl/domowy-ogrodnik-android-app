package com.example.domowyogrodnik.ui.list_plants

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.AddPlantActivity
import com.example.domowyogrodnik.R

class PlantsFragment : Fragment() {
    var button: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_plants, container, false)

        button = rootView.findViewById(R.id.button)

        button?.setOnClickListener { view ->
            Toast.makeText(view.context, "Plantsss!", Toast.LENGTH_SHORT).show()
            val intent = Intent (activity, AddPlantActivity::class.java)
            activity?.startActivity(intent)
        }

        return rootView
    }
}
