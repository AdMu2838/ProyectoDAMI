package com.example.proyecto.services

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.proyecto.R
import com.example.proyecto.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.navBottomNavigationView)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        AppBarConfiguration(navController.graph)
        navView.setupWithNavController(navController)
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

                        // Guardar los datos del perfil en SharedPreferences
                        val sharedPrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                        val editor = sharedPrefs.edit()
                        editor.putString("name", name)
                        editor.putString("email", email)
                        editor.putString("date", date)
                        editor.putString("phoneNumber", phoneNumber)
                        editor.apply()


                    }
                }
                .addOnFailureListener { exception ->
                    // Ocurrió un error al obtener los datos del perfil
                }
        }


    }

}