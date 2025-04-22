package com.example.miprimeraaplicacion;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String codigo;
    private String descripcion;
    private String presentacion;
    private String marca;
    private double precio;
    private String foto;
    private double costo;
    private double ganancia;
    private int stock;


    public Product() {
    }


    public Product(int id, String codigo, String descripcion, String presentacion, String marca,
                   double precio, String foto, double costo, double ganancia, int stock) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.presentacion = presentacion;
        this.marca = marca;
        this.precio = precio;
        this.foto = foto;
        this.costo = costo;
        this.ganancia = ganancia;
        this.stock = stock;
    }


    public Product(String codigo, String descripcion, String presentacion, String marca,
                   double precio, String foto, double costo, double ganancia, int stock) {
        this(0, codigo, descripcion, presentacion, marca, precio, foto, costo, ganancia, stock);
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public double getGanancia() { return ganancia; }
    public void setGanancia(double ganancia) { this.ganancia = ganancia; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
