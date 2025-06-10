package com.example.miprimeraaplicacion;

public class Registro {
    private String id;
    private String fecha;
    private String peso;
    private String imagenUri;
    private String audioPath;
    private String videoPath;

    public Registro() {}

    public Registro(String id, String fecha, String peso, String imagenUri, String audioPath, String videoPath) {
        this.id = id;
        this.fecha = fecha;
        this.peso = peso;
        this.imagenUri = imagenUri;
        this.audioPath = audioPath;
        this.videoPath = videoPath;
    }

    public String getId() { return id; }
    public String getFecha() { return fecha; }
    public String getPeso() { return peso; }
    public String getImagenUri() { return imagenUri; }
    public String getAudioPath() { return audioPath; }
    public String getVideoPath() { return videoPath; }
}
