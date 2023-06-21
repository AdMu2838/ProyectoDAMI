package com.example.proyecto.services

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import com.example.proyecto.R
import com.example.proyecto.adapters.ProductAdapter
import com.example.proyecto.core.Product
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
        // Configurar el GridView de productos
        productGridView = findViewById(R.id.gridView_products)
        productAdapter = ProductAdapter(this, products)
        productGridView.adapter = productAdapter

        // Obtener los productos de la categoría seleccionada
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("products")
            .whereEqualTo("category", categoryName)
            .orderBy("title", Query.Direction.ASCENDING)

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                val errorMessage = "Error al obtener los productos de la categoría: ${exception.message}"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                Log.e(TAG, errorMessage)
                return@addSnapshotListener
            }

            // Limpiar la lista de productos


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
                // Realizar la acción deseada al hacer clic en un producto
            }

        // Ocultar el GridView si no hay productos
        //productGridView.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
    }
}