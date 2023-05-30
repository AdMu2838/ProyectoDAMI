package com.example.proyecto.core;

public class Categoria {
    private String name;
    private int imageResId;

    public Categoria(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
