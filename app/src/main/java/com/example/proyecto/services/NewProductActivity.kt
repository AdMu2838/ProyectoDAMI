package com.example.proyecto.services

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.proyecto.R
import com.example.proyecto.core.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class NewProductActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etPrice: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnUploadPhoto: Button
    private lateinit var btnTakePhoto: Button

    private val categories = listOf("Accesorios", "Ropa", "Juguetes")
    private lateinit var selectedCategory: String

    // Constante para identificar el código de solicitud para seleccionar una imagen
    private val PICK_IMAGE_REQUEST = 1

    private val REQUEST_IMAGE_CAPTURE = 2

    // Uri de la imagen seleccionada
    private var imageUri: Uri? = null

    private lateinit var userID: String // ID del usuario actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_producto)
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        // Obtén el ID del usuario actual (puedes obtenerlo desde tu lógica de autenticación)
        userID = currentUser?.uid.toString()

        etTitle = findViewById(R.id.etTitle)
        etPrice = findViewById(R.id.etPrice)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSave)
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se seleccionó nada
            }
        }

        btnSave.setOnClickListener {
            saveProduct()
        }

        btnUploadPhoto.setOnClickListener {
            // Abre el selector de imágenes
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnTakePhoto.setOnClickListener {
            // Verificar si se tienen los permisos necesarios para acceder a la cámara
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 3)
            } else {
                // Abrir la cámara para tomar una foto
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } ?: Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Product Image",
            "Product Image"
        )
        imageUri = Uri.parse(savedImageURL)
    }

    private fun handleImageCaptureResult(data: Intent?) {
        val imageBitmap = data?.extras?.get("data") as? Bitmap
        if (imageBitmap != null) {
            // Guardar la imagen capturada en la galería
            saveImageToGallery(imageBitmap)
        } else {
            Toast.makeText(this, "No se pudo obtener la imagen capturada", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveProduct() {
        val title = etTitle.text.toString()
        val price = etPrice.text.toString()
        val phoneNumber = etPhoneNumber.text.toString()
        val description = etDescription.text.toString()

        // Validar los campos obligatorios
        if (title.isBlank() || price.isBlank() || phoneNumber.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si se ha seleccionado una imagen
        if (imageUri == null) {
            Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show()
            return
        }
        val productId = UUID.randomUUID().toString()
        // Crear un objeto Producto con los datos ingresados
        val product = Product(
            id = productId,
            title = title,
            price = price.toDouble(),
            phoneNumber = phoneNumber,
            description = description,
            category = selectedCategory,
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        )

        // Obtener una referencia al Storage de Firebase
        val storage = Firebase.storage
        // Crear una referencia al archivo en Firebase Storage
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}")

        // Subir la imagen al Storage de Firebase
        storageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Obtener la URL de descarga de la imagen
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Asignar la URL de la imagen al producto
                    product.imageUrl = uri.toString()

                    // Guardar el producto en Firebase Firestore
                    val db = Firebase.firestore
                    db.collection("products")
                        .add(product)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 3) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes proceder a acceder a la cámara y guardar la imagen
            } else {
                // Permiso denegado, muestra un mensaje al usuario o toma alguna otra acción
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Obtiene la URI de la imagen seleccionada
            imageUri = data.data
            // Mostrar la imagen seleccionada en un ImageView (opcional)
            //imageView.setImageURI(imageUri)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // Captura de foto exitosa, manejar la imagen capturada
            handleImageCaptureResult(data)
        }
    }
}

