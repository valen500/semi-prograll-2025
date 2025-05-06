package com.example.miprimeraaplicacion;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class MultimediaActivity extends AppCompatActivity {

    Button btnTomarFoto, btnSeleccionarImagen, btnGrabarAudio, btnReproducirAudio, btnReproducirVideo;
    ImageView imageView;
    VideoView videoView;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String audioPath;

    static final int REQUEST_FOTO = 1;
    static final int REQUEST_GALERIA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);

        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGrabarAudio = findViewById(R.id.btnGrabarAudio);
        btnReproducirAudio = findViewById(R.id.btnReproducirAudio);
        btnReproducirVideo = findViewById(R.id.btnReproducirVideo);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);

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
            try {
                audioPath = getExternalCacheDir().getAbsolutePath() + "/audio.3gp";
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(audioPath);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
                mediaRecorder.start();
                Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnReproducirAudio.setOnClickListener(v -> {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(this, "Reproduciendo audio", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnReproducirVideo.setOnClickListener(v -> {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw);
            videoView.setVideoURI(videoUri);
            videoView.start();
        });
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
}
