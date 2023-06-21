package com.example.proyecto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.proyecto.R
import com.example.proyecto.core.Categoria

class CategoryAdapter(private val mContext: Context, private val mCategories: List<Categoria>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return mCategories.size
    }

    override fun getItem(position: Int): Any {
        return mCategories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.category_image)
            holder.textView = view.findViewById(R.id.category_name)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val category = mCategories[position]
        holder.imageView?.setImageResource(category.imageResId)
        holder.textView?.text = category.name

        return view!!
    }

    private class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }
}