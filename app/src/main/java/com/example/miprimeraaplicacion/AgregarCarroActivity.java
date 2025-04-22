package com.example.miprimeraaplicacion;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AgregarCarroActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 101;
    private static final int REQUEST_AUDIO_PERMISSION = 102;

    private EditText etMarca, etModelo, etAnio;
    private ImageView imageCarro;
    private Button btnGuardar, btnCamara, btnGaleria, btnGrabarAudio;
    private Uri imagenUri;
    private CarroDbHelper dbHelper;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String rutaAudio = "";
    private boolean grabando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_carro);

        etMarca = findViewById(R.id.etMarca);
        etModelo = findViewById(R.id.etModelo);
        etAnio = findViewById(R.id.etAnio);
        imageCarro = findViewById(R.id.imageCarro);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCamara = findViewById(R.id.btnCamara);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnGrabarAudio = findViewById(R.id.btnGrabarAudio);
        Button btnReproducirAudio = findViewById(R.id.btnReproducirAudio);

        dbHelper = new CarroDbHelper(this);
        rutaAudio = getExternalFilesDir(null).getAbsolutePath() + "/audio_" + System.currentTimeMillis() + ".3gp";

        btnGuardar.setOnClickListener(v -> guardarCarro());
        btnCamara.setOnClickListener(v -> abrirCamara());
        btnGaleria.setOnClickListener(v -> abrirGaleria());

        btnGrabarAudio.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_AUDIO_PERMISSION);
            } else {
                alternarGrabacion(btnGrabarAudio);
            }
        });

        btnReproducirAudio.setOnClickListener(v -> reproducirAudio());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "canal_carros",
                    "Carros Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void alternarGrabacion(Button btn) {
        if (!grabando) {
            empezarGrabacion();
            btn.setText("Detener");
        } else {
            detenerGrabacion();
            btn.setText("Grabar Audio");
        }
        grabando = !grabando;
    }

    private void abrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            lanzarIntentCamara();
        }
    }

    private void lanzarIntentCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    private void abrirGaleria() {
        String permisoGaleria = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permisoGaleria)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permisoGaleria},
                    REQUEST_GALLERY_PERMISSION);
        } else {
            lanzarGaleria();
        }
    }

    private void lanzarGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bitmap foto = (Bitmap) data.getExtras().get("data");
                try {
                    File imageFile = new File(getFilesDir(), "carro_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    foto.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    imagenUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
                    imageCarro.setImageURI(imagenUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                imagenUri = data.getData();
                try {
                    String permisoGaleria = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                            Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

                    if (ContextCompat.checkSelfPermission(this, permisoGaleria)
                            == PackageManager.PERMISSION_GRANTED) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);
                        imageCarro.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(this, "Permiso de lectura no concedido", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void guardarCarro() {
        String marca = etMarca.getText().toString();
        String modelo = etModelo.getText().toString();
        String anioStr = etAnio.getText().toString();

        if (marca.isEmpty() || modelo.isEmpty() || anioStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int anio = Integer.parseInt(anioStr);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CarroDbHelper.COLUMN_MARCA, marca);
        values.put(CarroDbHelper.COLUMN_MODELO, modelo);
        values.put(CarroDbHelper.COLUMN_ANIO, anio);
        values.put("imagen", imagenUri != null ? imagenUri.toString() : null);
        values.put("audio", rutaAudio);

        long newRowId = db.insert(CarroDbHelper.TABLE_CARRO, null, values);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("carros");
        String id = ref.push().getKey();

        Map<String, Object> carroMap = new HashMap<>();
        carroMap.put("marca", marca);
        carroMap.put("modelo", modelo);
        carroMap.put("anio", anio);
        carroMap.put("imagen", imagenUri != null ? imagenUri.toString() : null);
        carroMap.put("audio", rutaAudio);

        if (id != null) {
            ref.child(id).setValue(carroMap);
        }

        enviarCarroWebService(marca, modelo, anioStr);

        if (newRowId != -1) {
            Toast.makeText(this, "Carro guardado correctamente", Toast.LENGTH_SHORT).show();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canal_carros")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Carro guardado")
                    .setContentText("El carro " + marca + " " + modelo + " ha sido registrado.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1001, builder.build());

            finish();
        } else {
            Toast.makeText(this, "Error al guardar el carro", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarCarroWebService(final String marca, final String modelo, final String anio) {
        new Thread(() -> {
            try {
                URL url = new URL("https://tuservidor.com/api/carros");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("marca", marca);
                jsonParam.put("modelo", modelo);
                jsonParam.put("anio", anio);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("WebService", "Response Code: " + responseCode);
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void empezarGrabacion() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(rutaAudio);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerGrabacion() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(this, "Grabación finalizada", Toast.LENGTH_SHORT).show();
        }
    }

    private void reproducirAudio() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(rutaAudio);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Reproduciendo...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarIntentCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarGaleria();
            } else {
                Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                alternarGrabacion(btnGrabarAudio);
            } else {
                Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
