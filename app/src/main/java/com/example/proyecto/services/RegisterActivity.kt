package com.example.proyecto.services

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.example.proyecto.R
import com.example.proyecto.config.DatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {
    private lateinit var btnPickDate: EditText
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnPickDate = findViewById(R.id.editTextDate)

        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    btnPickDate.setText(selectedDate)
                }, year, month, dayOfMonth)

            datePickerDialog.show()
        }
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            finish()
        }
        // Inicializar el DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // ...

        val btnRegister: Button = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener { registerUser() }
    }
    private fun registerUser() {
        val name = findViewById<EditText>(R.id.editTextName).text.toString().trim()
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString().trim()
        val date = findViewById<EditText>(R.id.editTextDate).text.toString().trim()
        val phoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber).text.toString().trim()

        // Validar los campos de entrada
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Ingresa tu nombre", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        // Registrar el usuario en Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El registro fue exitoso, el usuario está autenticado
                    val user = FirebaseAuth.getInstance().currentUser

                    // Realizar cualquier acción adicional necesaria (por ejemplo, guardar datos adicionales del usuario en Firestore)

                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    // Ocurrió un error durante el registro
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}