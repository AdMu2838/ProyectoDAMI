package com.example.proyecto.services

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyecto.R
import com.example.proyecto.config.DatabaseHelper

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
        // Obtener los datos del formulario de inicio de sesión
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        // Buscar el usuario en la base de datos
        val db = databaseHelper.readableDatabase
        val columns = arrayOf(DatabaseHelper.COLUMN_ID)
        val selection = "${DatabaseHelper.COLUMN_EMAIL} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?"
        val selectionArgs = arrayOf(email, password)
        val cursor: Cursor? = db.query(
            DatabaseHelper.TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            // El usuario existe y las credenciales son correctas
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
            Toast.makeText(this, "Inicio de sesión exitoso. ID de usuario: $userId", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // Las credenciales son incorrectas o el usuario no existe
            Toast.makeText(this, "Credenciales incorrectas. Por favor, intenta nuevamente.", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
    }

}