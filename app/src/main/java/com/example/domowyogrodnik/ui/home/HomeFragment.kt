package com.example.domowyogrodnik.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.R

class HomeFragment : Fragment() {
    var button: Button? = null
    var textView_hello: TextView? = null
    var textView_stats: TextView? = null
    var logo: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        button = rootView.findViewById(R.id.button)
        textView_hello = rootView.findViewById(R.id.text_home)
        textView_stats = rootView.findViewById(R.id.text_home2)
        logo = rootView.findViewById(R.id.cardView2)

        button?.setOnClickListener { view ->
            Toast.makeText(view.context, "Kliknięto!", Toast.LENGTH_SHORT).show()
        }

        textView_hello?.text = getString(R.string.description)
        textView_stats?.text = "Tutaj statystyki"

        val animationFadeIn = AnimationUtils.loadAnimation(rootView.context, R.anim.fade)
        logo?.startAnimation(animationFadeIn)
        textView_hello?.startAnimation(animationFadeIn)
        textView_stats?.startAnimation(animationFadeIn)
        button?.startAnimation(animationFadeIn)

        return rootView
    }
}
