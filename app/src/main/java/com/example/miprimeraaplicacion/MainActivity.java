package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.media.MediaPlayer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView tempVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempVal = findViewById(R.id.lblReproductorMusica);
        reproductorMusica();
    }

    void reproductorMusica() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.audio);
        mediaPlayer.start();
    }
}
