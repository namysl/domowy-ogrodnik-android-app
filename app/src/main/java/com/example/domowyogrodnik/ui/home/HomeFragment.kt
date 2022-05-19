package com.example.domowyogrodnik.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.R

class HomeFragment : Fragment() {
    private var button: Button? = null
    private var textViewHello: TextView? = null
    private var textViewStats: TextView? = null
    private var logo: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        button = rootView.findViewById(R.id.button)
        textViewHello = rootView.findViewById(R.id.text_home)
        textViewStats = rootView.findViewById(R.id.text_home2)
        logo = rootView.findViewById(R.id.imageView_logo)

        button?.setOnClickListener { view ->
            Toast.makeText(view.context, "Kliknięto!", Toast.LENGTH_SHORT).show()
        }

        textViewHello?.text = getString(R.string.template)
        textViewStats?.text = getString(R.string.template_stats)

        //animacja zdecydowanie nie jest overkillem!
        val animation = AnimationUtils.loadAnimation(rootView.context, R.anim.turn)
        logo?.startAnimation(animation)
        textViewHello?.startAnimation(animation)
        textViewStats?.startAnimation(animation)
        button?.startAnimation(animation)

        return rootView
    }
}
