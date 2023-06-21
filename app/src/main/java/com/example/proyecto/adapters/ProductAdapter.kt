package com.example.proyecto.adapters
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.proyecto.R
import com.example.proyecto.core.Product
import com.squareup.picasso.Picasso

class ProductAdapter(private val context: Context, private val products: List<Product>) : BaseAdapter() {

    override fun getCount(): Int {
        return products.size
    }

    override fun getItem(position: Int): Any {
        return products[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_product, parent, false)
            holder = ViewHolder()
            holder.productImage = view.findViewById(R.id.productImage)
            holder.productTitle = view.findViewById(R.id.productTitle)
            holder.productPrice = view.findViewById(R.id.productPrice)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val product = products[position]
        // Cargar la imagen del producto utilizando Picasso
        holder.productImage?.let { imageView ->
            Picasso.get()
                .load(product.imageUrl)
                .into(imageView)
        }
        // Mostrar los datos del producto en los elementos de la vista
        holder.productTitle?.text = product.title
        holder.productPrice?.text = product.price.toString()



        return view!!
    }

    private class ViewHolder {
        var productImage: ImageView? = null
        var productTitle: TextView? = null
        var productPrice: TextView? = null
    }
}