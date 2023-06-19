package com.example.proyecto.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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
        // Obtener el usuario actualmente autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val date = document.getString("date")
                        val phoneNumber = document.getString("phoneNumber")

                        // Actualiza la interfaz de usuario con los datos del perfil
                        updateProfileUI(name, email, date, phoneNumber)
                    }
                }
                .addOnFailureListener { exception ->
                    // Ocurrió un error al obtener los datos del perfil
                }
        }
        return view
    }
    private fun updateProfileUI(name: String?, email: String?, date: String?, phoneNumber: String?) {
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