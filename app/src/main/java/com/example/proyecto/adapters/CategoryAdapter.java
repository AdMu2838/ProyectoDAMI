package com.example.proyecto.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyecto.R;
import com.example.proyecto.core.Categoria;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<Categoria> mCategories;

    public CategoryAdapter(Context context, List<Categoria> categories) {
        mContext = context;
        mCategories = categories;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.category_image);
            holder.textView = convertView.findViewById(R.id.category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Categoria categoria = mCategories.get(position);
        holder.imageView.setImageResource(categoria.getImageResId());
        holder.textView.setText(categoria.getName());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
