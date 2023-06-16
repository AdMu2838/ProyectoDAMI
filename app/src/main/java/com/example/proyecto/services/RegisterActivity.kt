package com.example.proyecto.services

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.example.proyecto.R
import com.example.proyecto.config.DatabaseHelper
import java.util.Calendar

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
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val editTextDate: EditText = findViewById(R.id.editTextDate)
        val editTextPhoneNumber: EditText = findViewById(R.id.editTextPhoneNumber)

        val name = editTextName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        val birthdate = editTextDate.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()

        // Insertar los datos en la base de datos
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NAME, name)
            put(DatabaseHelper.COLUMN_EMAIL, email)
            put(DatabaseHelper.COLUMN_PASSWORD, password)
            put(DatabaseHelper.COLUMN_BIRTHDATE, birthdate)
            put(DatabaseHelper.COLUMN_PHONE, phoneNumber)
        }
        val newRowId = db?.insert(DatabaseHelper.TABLE_NAME, null, values)

        if (newRowId != -1L) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
        }
    }
}