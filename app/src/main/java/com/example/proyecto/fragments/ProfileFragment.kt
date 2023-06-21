package com.example.proyecto.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.R
import com.example.proyecto.services.LoginActivity
import com.example.proyecto.services.ProductListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {


    val auth = FirebaseAuth.getInstance()
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

        // Obtener una referencia al botón "Mis Productos"
        val btnMyProducts = view.findViewById<Button>(R.id.btnMyProducts)

        // Agregar el listener de clic al botón "Mis Productos"
        btnMyProducts.setOnClickListener {
            openMyProducts()
        }

        // Obtener una referencia al botón "Cerrar Sesión"
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // Agregar el listener de clic al botón "Cerrar Sesión"
        btnLogout.setOnClickListener {
            signOut()
        }

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

    private fun openMyProducts() {
        // Obtener el ID del usuario actual
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        // Crear el intent para abrir la actividad "ProductListActivity" con el ID del usuario actual
        val intent = Intent(context, ProductListActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun signOut() {
        auth.signOut()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}