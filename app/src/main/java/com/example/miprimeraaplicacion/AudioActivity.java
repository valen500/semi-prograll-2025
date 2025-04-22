package com.example.miprimeraaplicacion;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;

public class AudioActivity extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String rutaAudio;
    private Button btnGrabar, btnReproducir;
    private boolean grabando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        btnGrabar = findViewById(R.id.btnGrabar);
        btnReproducir = findViewById(R.id.btnReproducir);

        // Ruta mejorada con timestamp para evitar sobreescrituras
        String timestamp = String.valueOf(System.currentTimeMillis());
        rutaAudio = getExternalFilesDir(null).getAbsolutePath() + "/grabacion_" + timestamp + ".mp4";

        btnGrabar.setOnClickListener(v -> toggleGrabacion());
        btnReproducir.setOnClickListener(v -> reproducirAudio());
    }

    private void toggleGrabacion() {
        if (!grabando) {
            empezarGrabacion();
            btnGrabar.setText("Detener Grabación");
            btnReproducir.setEnabled(false);
        } else {
            detenerGrabacion();
            btnGrabar.setText("Iniciar Grabación");
            btnReproducir.setEnabled(true);
        }
        grabando = !grabando;
    }

    private void empezarGrabacion() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(rutaAudio);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("AudioActivity", "Error grabación: " + e.getMessage());
            Toast.makeText(this, "Error al grabar", Toast.LENGTH_LONG).show();
        }
    }

    private void detenerGrabacion() {
        try {
            mediaRecorder.stop();
            Toast.makeText(this, "Grabación guardada en: " + rutaAudio, Toast.LENGTH_LONG).show();
        } catch (RuntimeException e) {
            Log.e("AudioActivity", "Error deteniendo: " + e.getMessage());
        }
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void reproducirAudio() {
        if (rutaAudio == null || !new File(rutaAudio).exists()) {
            Toast.makeText(this, "No hay grabación disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(rutaAudio);
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                Toast.makeText(AudioActivity.this, "Reproducción completada", Toast.LENGTH_SHORT).show();
            });
        } catch (IOException e) {
            Log.e("AudioActivity", "Error reproducción: " + e.getMessage());
            Toast.makeText(this, "Error al reproducir", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public String getRutaAudio() {
        return "file://" + rutaAudio;
    }
}