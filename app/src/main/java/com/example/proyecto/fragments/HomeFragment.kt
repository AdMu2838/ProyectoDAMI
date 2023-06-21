package com.example.proyecto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyecto.R
import com.example.proyecto.adapters.CategoryAdapter
import com.example.proyecto.core.Categoria
import com.example.proyecto.services.NewProductActivity
import com.example.proyecto.services.ProductListActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private lateinit var categoryGridView: GridView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categories: MutableList<Categoria> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        categoryGridView = view.findViewById(R.id.gridView_categories)

        // Agregar las categorías solo si la lista está vacía
        if (categories.isEmpty()) {
            categories.add(Categoria("Accesorios", R.drawable.categoria1_image))
            categories.add(Categoria("Ropa", R.drawable.categoria2_image))
            categories.add(Categoria("Juguetes", R.drawable.categoria3_image))
        }

        // Configurar el adaptador de categorías
        categoryAdapter = CategoryAdapter(requireContext(), categories)
        categoryGridView.adapter = categoryAdapter

        // Configurar el clic en una categoría
        categoryGridView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val category = categories[position]
                val intent = Intent(requireContext(), ProductListActivity::class.java)
                intent.putExtra("categoryName", category.name)
                startActivity(intent)
            }

        // Configurar el botón de agregar
        val addButton: FloatingActionButton = view.findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(requireContext(), NewProductActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}