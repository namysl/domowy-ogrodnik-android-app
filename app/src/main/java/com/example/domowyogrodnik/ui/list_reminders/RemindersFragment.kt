package com.example.domowyogrodnik.ui.list_reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.domowyogrodnik.R

class RemindersFragment : Fragment() {
    var button: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_reminders, container, false)

        button = rootView.findViewById(R.id.button)

        button?.setOnClickListener { view ->
            Toast.makeText(view.context, "Remindersss!", Toast.LENGTH_SHORT).show()
        }

        return rootView
    }
}
