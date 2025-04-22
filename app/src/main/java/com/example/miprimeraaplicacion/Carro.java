package com.example.miprimeraaplicacion;

public class Carro {
    private int id;
    private String marca;
    private String modelo;
    private int anio;
    private String imagen; // 🆕 NUEVO campo

    public Carro() {}

    public Carro(int id, String marca, String modelo, int anio) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
