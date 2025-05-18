// MultimediaActivity.java
package com.example.miprimeraaplicacion;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class MultimediaActivity extends AppCompatActivity {

    EditText etFecha, etPeso;
    Button btnTomarFoto, btnSeleccionarImagen, btnGrabarAudio, btnDetenerGrabacion,
            btnReproducirAudio, btnReproducirVideo, btnGuardarRegistro;
    ImageView imageView;
    VideoView videoView;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String audioPath;
    boolean isRecording = false;

    static final int REQUEST_FOTO = 1;
    static final int REQUEST_GALERIA = 2;

    DatabaseHelper dbHelper;
    DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);

        // Enlazar vistas
        etFecha = findViewById(R.id.etFecha);
        etPeso = findViewById(R.id.etPeso);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGrabarAudio = findViewById(R.id.btnGrabarAudio);
        btnDetenerGrabacion = findViewById(R.id.btnDetenerGrabacion);
        btnReproducirAudio = findViewById(R.id.btnReproducirAudio);
        btnReproducirVideo = findViewById(R.id.btnReproducirVideo);
        btnGuardarRegistro = findViewById(R.id.btnGuardarRegistro);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);

        dbHelper = new DatabaseHelper(this);
        firebaseRef = FirebaseDatabase.getInstance().getReference("registros");

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 1);

        btnTomarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_FOTO);
        });

        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_GALERIA);
        });

        btnGrabarAudio.setOnClickListener(v -> {
            if (!isRecording) iniciarGrabacion();
        });

        btnDetenerGrabacion.setOnClickListener(v -> {
            if (isRecording) detenerGrabacion();
        });

        btnReproducirAudio.setOnClickListener(v -> reproducirAudio());

        btnReproducirVideo.setOnClickListener(v -> {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw);
            videoView.setVideoURI(videoUri);
            videoView.start();
        });

        btnGuardarRegistro.setOnClickListener(v -> {
            String fecha = etFecha.getText().toString();
            String peso = etPeso.getText().toString();

            if (fecha.isEmpty() || peso.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar en SQLite
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("fecha", fecha);
                values.put("peso", peso);
                db.insert("registro", null, values);

                // Guardar en Firebase
                String id = firebaseRef.push().getKey();
                Registro registro = new Registro(id, fecha, peso);
                if (id != null) {
                    firebaseRef.child(id).setValue(registro);
                }

                Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
            }
        });

        btnReproducirAudio.setText("Reproducir Audio");
        btnReproducirAudio.setEnabled(false);
        btnDetenerGrabacion.setEnabled(false);
        btnDetenerGrabacion.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private void iniciarGrabacion() {
        try {
            audioPath = getExternalCacheDir().getAbsolutePath() + "/audio_" + System.currentTimeMillis() + ".3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioPath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            btnGrabarAudio.setEnabled(false);
            btnDetenerGrabacion.setEnabled(true);
            btnDetenerGrabacion.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            btnReproducirAudio.setEnabled(false);
            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerGrabacion() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            btnGrabarAudio.setEnabled(true);
            btnDetenerGrabacion.setEnabled(false);
            btnDetenerGrabacion.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            btnReproducirAudio.setEnabled(true);
            Toast.makeText(this, "Grabación finalizada", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al detener grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void reproducirAudio() {
        if (audioPath == null) {
            Toast.makeText(this, "No hay audio grabado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            btnReproducirAudio.setText("Reproduciendo...");
            btnReproducirAudio.setEnabled(false);

            mediaPlayer.setOnCompletionListener(mp -> {
                btnReproducirAudio.setText("Reproducir Audio");
                btnReproducirAudio.setEnabled(true);
                Toast.makeText(this, "Reproducción completada", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) detenerGrabacion();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            btnReproducirAudio.setText("Reproducir Audio");
            btnReproducirAudio.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_FOTO) {
                Bitmap foto = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(foto);
            } else if (requestCode == REQUEST_GALERIA) {
                Uri imagen = data.getData();
                imageView.setImageURI(imagen);
            }
        }
    }

    private void limpiarFormulario() {
        etFecha.setText("");
        etPeso.setText("");
        imageView.setImageDrawable(null);
        videoView.stopPlayback();
        audioPath = null;

        btnReproducirAudio.setText("Reproducir Audio");
        btnReproducirAudio.setEnabled(false);
        btnDetenerGrabacion.setEnabled(false);
        btnDetenerGrabacion.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }
}
