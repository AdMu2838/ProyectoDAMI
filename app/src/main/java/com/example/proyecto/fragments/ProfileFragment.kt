package com.example.proyecto.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Leer los datos del perfil desde SharedPreferences
        val sharedPrefs = context?.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val name = sharedPrefs?.getString("name", "")
        val email = sharedPrefs?.getString("email", "")
        val date = sharedPrefs?.getString("date", "")
        val phoneNumber = sharedPrefs?.getString("phoneNumber", "")

        // Actualizar la interfaz de usuario con los datos del perfil
        updateProfileUI(view, name, email, date, phoneNumber)

        return view
    }
    private fun updateProfileUI(view: View?, name: String?, email: String?, date: String?, phoneNumber: String?) {
        val nameTextView = view?.findViewById<TextView>(R.id.nameTextView)
        val emailTextView = view?.findViewById<TextView>(R.id.emailTextView)
        val dateTextView = view?.findViewById<TextView>(R.id.dateTextView)
        val phoneNumberTextView = view?.findViewById<TextView>(R.id.phoneNumberTextView)

        nameTextView?.text = name
        emailTextView?.text = email
        dateTextView?.text = date
        phoneNumberTextView?.text = phoneNumber
    }
}