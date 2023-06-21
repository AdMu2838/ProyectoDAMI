package com.example.proyecto.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyecto.R
import com.example.proyecto.adapters.CategoryAdapter
import com.example.proyecto.core.Categoria
import com.example.proyecto.services.NewProductActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private lateinit var categoryGridView: GridView
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        categoryGridView = view.findViewById(R.id.gridView_categories)
        val categories: MutableList<Categoria> = ArrayList()
        categories.add(Categoria("Categoría 1", R.drawable.categoria1_image))
        categories.add(Categoria("Categoría 2", R.drawable.categoria2_image))
        categories.add(Categoria("Categoría 3", R.drawable.categoria3_image))

        categoryAdapter = CategoryAdapter(requireContext(), categories)

        // Asignar el adaptador al GridView
        categoryGridView.adapter = categoryAdapter

        // Configurar el botón de agregar
        val addButton: FloatingActionButton = view.findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(requireContext(), NewProductActivity::class.java)
            startActivity(intent)
        }

        return view
    }

}