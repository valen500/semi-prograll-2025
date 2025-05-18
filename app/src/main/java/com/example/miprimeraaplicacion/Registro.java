package com.example.miprimeraaplicacion;

public class Registro {
    private String id;
    private String fecha;
    private String peso;
    private String imagenUri;
    private String audioPath;
    private String videoUri;

    public Registro() {}

    //  6 campos
    public Registro(String id, String fecha, String peso, String imagenUri, String audioPath, String videoUri) {
        this.id = id;
        this.fecha = fecha;
        this.peso = peso;
        this.imagenUri = imagenUri;
        this.audioPath = audioPath;
        this.videoUri = videoUri;
    }

    //  solo 3 campos (usado en SQLite y Firebase)
    public Registro(String id, String fecha, String peso) {
        this.id = id;
        this.fecha = fecha;
        this.peso = peso;
        this.imagenUri = "";
        this.audioPath = "";
        this.videoUri = "";
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getPeso() { return peso; }
    public void setPeso(String peso) { this.peso = peso; }

    public String getImagenUri() { return imagenUri; }
    public void setImagenUri(String imagenUri) { this.imagenUri = imagenUri; }

    public String getAudioPath() { return audioPath; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }

    public String getVideoUri() { return videoUri; }
    public void setVideoUri(String videoUri) { this.videoUri = videoUri; }
}
