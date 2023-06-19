package com.example.proyecto.services

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyecto.R
import com.example.proyecto.config.DatabaseHelper
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        databaseHelper = DatabaseHelper(this)
        val btnRegister = findViewById<Button>(R.id.btnRegistro)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener{loginUser()

        }
    }
    private fun loginUser() {
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString().trim()

            // Validar los campos de entrada
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                return
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
                return
            }

            // Iniciar sesión con Firebase Authentication
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // El inicio de sesión fue exitoso, el usuario está autenticado
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Ocurrió un error durante el inicio de sesión
                        Toast.makeText(
                            this,
                            "Error al iniciar sesión: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
}