package com.example.proyecto.services

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
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

        val showChatButton = userId != null && userId != product.userId

        if (showChatButton) {
            dialogBuilder.setNeutralButton("Chat") { _, _ ->
                startChatWithProductOwner(product)
            }
        }

        if (userId == product.userId) {
            dialogBuilder.setNegativeButton("Eliminar") { _, _ ->
                deleteProduct(product)
            }
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun startChatWithProductOwner(product: Product) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("productId", product.id)
        intent.putExtra("productOwner", product.userId)
        startActivity(intent)
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