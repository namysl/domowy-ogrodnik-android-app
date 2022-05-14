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
    var textView: TextView? = null
    var logo: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        button = rootView.findViewById(R.id.button)
        textView = rootView.findViewById(R.id.text_home)
        logo = rootView.findViewById(R.id.cardView2)

        button?.setOnClickListener { view ->
            Toast.makeText(view.context, "Kliknięto!", Toast.LENGTH_SHORT).show()
        }

        textView?.text = getString(R.string.description)

        val animationFadeIn = AnimationUtils.loadAnimation(rootView.context, R.anim.fade)
        logo?.startAnimation(animationFadeIn)


        return rootView
    }
}
