package com.example.miprimeraaplicacion;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MultimediaActivity extends AppCompatActivity {

    EditText etFecha, etPeso;
    Button btnTomarFoto, btnSeleccionarImagen, btnGrabarAudio, btnDetenerGrabacion,
            btnReproducirAudio, btnReproducirVideo, btnGuardarRegistro;
    ImageView imageView;
    VideoView videoView;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String audioPath;
    String imagenUri = "";
    boolean isRecording = false;

    static final int REQUEST_FOTO = 1;
    static final int REQUEST_GALERIA = 2;

    Uri fotoUri;

    DatabaseHelper dbHelper;
    DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES
            }, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        }


        etFecha.setFocusable(false);
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        btnTomarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile;
                try {
                    photoFile = createImageFile();
                    fotoUri = FileProvider.getUriForFile(
                            this,
                            "com.example.miprimeraaplicacion.fileprovider",
                            photoFile
                    );
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                    startActivityForResult(intent, REQUEST_FOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
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

            // Asegurar que el VideoView se muestra completo
            videoView.setOnPreparedListener(mp -> {
                // Mantener proporción o ajustar si es necesario
                mp.setOnVideoSizeChangedListener((mp1, width, height) -> {
                    // Esto fuerza el video a ocupar el tamaño definido en el layout
                    videoView.setScaleX(1.0f);
                    videoView.setScaleY(1.0f);
                });

                videoView.start();
            });

            videoView.setZOrderOnTop(false);
        });


        btnGuardarRegistro.setOnClickListener(v -> {
            String fecha = etFecha.getText().toString();
            String peso = etPeso.getText().toString();

            if (fecha.isEmpty() || peso.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("fecha", fecha);
                values.put("peso", peso);
                values.put("imagenUri", imagenUri);
                values.put("audioPath", audioPath);
                db.insert("registro", null, values);

                String id = firebaseRef.push().getKey();
                Registro registro = new Registro(
                        id,
                        fecha,
                        peso,
                        imagenUri != null ? imagenUri : "",
                        audioPath != null ? audioPath : "",
                        ""
                );

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
        btnDetenerGrabacion.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.GRAY));
    }

    private void mostrarDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String fechaFormateada = String.format("%02d / %02d / %04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etFecha.setText(fechaFormateada);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalCacheDir();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imagenUri = Uri.fromFile(image).toString();
        return image;
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
            btnDetenerGrabacion.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.RED));
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
            btnDetenerGrabacion.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.GRAY));
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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FOTO) {
                imageView.setImageURI(fotoUri);
                imagenUri = fotoUri.toString();
            } else if (requestCode == REQUEST_GALERIA && data != null) {
                Uri imagenSeleccionada = data.getData();
                if (imagenSeleccionada != null) {
                    getContentResolver().takePersistableUriPermission(
                            imagenSeleccionada,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    imageView.setImageURI(imagenSeleccionada);
                    imagenUri = imagenSeleccionada.toString();
                }
            }
        }
    }

    private void limpiarFormulario() {
        etFecha.setText("");
        etPeso.setText("");
        imageView.setImageResource(R.drawable.descarga);
        audioPath = null;
        imagenUri = "";
        btnReproducirAudio.setEnabled(false);
    }
}
