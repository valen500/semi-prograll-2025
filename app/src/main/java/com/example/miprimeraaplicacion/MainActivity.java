package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView tempVal;
    Button btnIniciar, btnPausar, btnParar;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de vistas
        tempVal = findViewById(R.id.lblReproductorMusica);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnPausar = findViewById(R.id.btnPausar);
        btnParar = findViewById(R.id.btnParar);

        // Inicializar el reproductor de música
        reproductorMusica();

        // Definir la acción del botón Iniciar
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar();
            }
        });

        // Definir la acción del botón Pausar
        btnPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausar();
            }
        });

        // Definir la acción del botón Parar
        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detener();
            }
        });
    }

    // Método para inicializar el reproductor de música
    void reproductorMusica() {
        // Si ya existe un MediaPlayer, lo liberamos antes de crear uno nuevo
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.audio);
    }

    // Método para iniciar la reproducción
    void iniciar() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            tempVal.setText("Reproduciendo...");
        }
    }

    // Método para pausar la reproducción
    void pausar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            tempVal.setText("Pausado...");
        }
    }

    // Método para detener la reproducción
    void detener() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            tempVal.setText("Detenido...");
            reproductorMusica(); // Reiniciar el reproductor
        }
    }

    @Override
    protected void onDestroy() {
        // Liberar recursos cuando la actividad sea destruida
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
