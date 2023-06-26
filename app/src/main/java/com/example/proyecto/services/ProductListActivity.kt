package com.example.proyecto.services

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.proyecto.R
import com.example.proyecto.adapters.ProductAdapter
import com.example.proyecto.core.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProductListActivity : AppCompatActivity() {
    private lateinit var productGridView: GridView
    private lateinit var productAdapter: ProductAdapter
    private val products: MutableList<Product> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val categoryName = intent.getStringExtra("categoryName")
        val userId = intent.getStringExtra("userId") // Obtener el userId de los parámetros

        // Configurar el GridView de productos
        productGridView = findViewById(R.id.gridView_products)
        productAdapter = ProductAdapter(this, products)
        productGridView.adapter = productAdapter

        // Obtener los productos de la categoría seleccionada y del usuario si se proporciona el userId
        val firestore = FirebaseFirestore.getInstance()
        var query: Query = firestore.collection("products")

        if (userId != null) {
            query = query.whereEqualTo("userId", userId)
        } else if (categoryName != null) {
            query = query.whereEqualTo("category", categoryName)
        }

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                val errorMessage = "Error al obtener los productos de la categoría: ${exception.message}"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, errorMessage)
                return@addSnapshotListener
            }

            // Limpiar la lista de productos
            products.clear()

            if (snapshot != null) {
                Log.d(TAG, "Número de documentos recuperados: ${snapshot.documents.size}")
                for (document in snapshot) {
                    val product = document.toObject(Product::class.java)
                    products.add(product)
                }
                Log.d(TAG, "Número de productos obtenidos: ${products.size}")
            }

            // Notificar al adaptador que los datos han cambiado
            productAdapter.notifyDataSetChanged()
        }

        // Configurar el clic en un producto
        productGridView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedProduct = products[position]
                showProductDetails(selectedProduct)
            }

        // Ocultar el GridView si no hay productos
        //productGridView.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showProductDetails(product: Product) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Detalles del producto")
        dialogBuilder.setMessage("Nombre: ${product.title}\n" +
                "Precio: ${product.price}\n" +
                "Descripción: ${product.description}\n" +
                "Número de contacto: ${product.phoneNumber}")
        dialogBuilder.setPositiveButton("Cerrar", null)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId == product.userId) {
            // El usuario actual tiene el mismo userId que el producto, se puede mostrar el botón "Eliminar"
            dialogBuilder.setNegativeButton("Eliminar") { _, _ ->
                deleteProduct(product)
            }

            dialogBuilder.setNeutralButton("Editar") { _, _ ->
                editProduct(product)
            }
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun editProduct(product: Product) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Editar producto")

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_product, null)
        dialogBuilder.setView(dialogView)

        val titleEditText: EditText = dialogView.findViewById(R.id.editText_title)
        val priceEditText: EditText = dialogView.findViewById(R.id.editText_price)
        val descriptionEditText: EditText = dialogView.findViewById(R.id.editText_description)
        val contactEditText: EditText = dialogView.findViewById(R.id.editText_contact)
        val spinnerCategory: Spinner = dialogView.findViewById(R.id.spinnerCategory)
        val categoryArray = resources.getStringArray(R.array.category_array)

        val initialCategoryIndex = categoryArray.indexOf(product.category)
        spinnerCategory.setSelection(initialCategoryIndex)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Retrieve the selected category and update the product object
                val selectedCategory = categoryArray[position]
                product.category = selectedCategory
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when no category is selected
            }
        }

        // Mostrar los detalles actuales del producto en los campos de edición
        titleEditText.setText(product.title)
        priceEditText.setText(product.price.toString())
        descriptionEditText.setText(product.description)
        contactEditText.setText(product.phoneNumber)

        dialogBuilder.setPositiveButton("Guardar") { _, _ ->
            // Obtener los nuevos valores del producto editado
            val newTitle = titleEditText.text.toString()
            val newPrice = priceEditText.text.toString().toDoubleOrNull()
            val newDescription = descriptionEditText.text.toString()
            val newContact = contactEditText.text.toString()

            val firestore = FirebaseFirestore.getInstance()

            // Realiza una consulta para obtener el documento con el campo product.id
            firestore.collection("products")
                .whereEqualTo("id", product.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Actualizar los detalles del producto en la lista local
                        product.title = newTitle
                        if (newPrice != null) {
                            product.price = newPrice
                        }
                        product.description = newDescription
                        product.phoneNumber = newContact

                        // Actualizar los detalles del producto en Firebase Firestore
                        val productRef = firestore.collection("products").document(document.id)
                        val updatedData = mapOf(
                            "title" to newTitle,
                            "price" to newPrice,
                            "description" to newDescription,
                            "phoneNumber" to newContact,
                            "category" to product.category
                        )
                        productRef.update(updatedData)
                            .addOnSuccessListener {
                                // Actualización exitosa en Firebase
                                // Notificar al adaptador que los datos han cambiado
                                productAdapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener { exception ->
                                // Error al actualizar el producto en Firebase
                                Toast.makeText(this, "Error al actualizar el producto: ${exception.message}", Toast.LENGTH_SHORT).show()
                                Log.e(TAG, "Error al actualizar el producto", exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // Error al obtener el documento en Firebase
                    Toast.makeText(this, "Error al obtener el producto: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error al obtener el producto", exception)
                }
        }

        dialogBuilder.setNegativeButton("Cancelar", null)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun deleteProduct(product: Product) {
        val firestore = FirebaseFirestore.getInstance()

        // Realiza una consulta para obtener el documento con el campo product.id
        firestore.collection("products")
            .whereEqualTo("id", product.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Elimina el documento correspondiente
                    document.reference.delete()
                        .addOnSuccessListener {
                            // Eliminación exitosa en Firebase, ahora elimina el producto de la lista local
                            products.remove(product)
                            productAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            // Error al eliminar el producto de Firebase
                            Toast.makeText(this, "Error al eliminar el producto: ${exception.message}", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Error al eliminar el producto", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Error al obtener el documento en Firebase
                Toast.makeText(this, "Error al obtener el producto: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error al obtener el producto", exception)
            }
    }



    companion object {
        private const val TAG = "ProductListActivity"
    }
}