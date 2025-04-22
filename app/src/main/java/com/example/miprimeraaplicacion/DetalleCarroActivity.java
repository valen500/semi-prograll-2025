package com.example.miprimeraaplicacion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class DetalleCarroActivity extends AppCompatActivity {

    TextView txtMarca, txtModelo, txtAnio;
    ImageView imageCarro;
    Button btnReproducirAudio;

    String rutaAudio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_carro);

        txtMarca = findViewById(R.id.txtMarca);
        txtModelo = findViewById(R.id.txtModelo);
        txtAnio = findViewById(R.id.txtAnio);
        imageCarro = findViewById(R.id.imageCarro);
        btnReproducirAudio = findViewById(R.id.btnReproducirAudio);

        long idCarro = getIntent().getLongExtra("idCarro", -1);

        if (idCarro != -1) {
            cargarDatosCarro(idCarro);
        }

        btnReproducirAudio.setOnClickListener(v -> {
            if (!rutaAudio.isEmpty()) {
                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(rutaAudio);
                    player.prepare();
                    player.start();
                    Toast.makeText(this, "Reproduciendo audio...", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al reproducir", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No hay audio disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosCarro(long id) {
        SQLiteDatabase db = new CarroDbHelper(this).getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + CarroDbHelper.TABLE_CARRO + " WHERE " + CarroDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            String marca = cursor.getString(cursor.getColumnIndexOrThrow(CarroDbHelper.COLUMN_MARCA));
            String modelo = cursor.getString(cursor.getColumnIndexOrThrow(CarroDbHelper.COLUMN_MODELO));
            int anio = cursor.getInt(cursor.getColumnIndexOrThrow(CarroDbHelper.COLUMN_ANIO));
            String imagenPath = cursor.getString(cursor.getColumnIndexOrThrow(CarroDbHelper.COLUMN_IMAGEN));
            rutaAudio = cursor.getString(cursor.getColumnIndexOrThrow(CarroDbHelper.COLUMN_AUDIO));

            txtMarca.setText("Marca: " + marca);
            txtModelo.setText("Modelo: " + modelo);
            txtAnio.setText("Año: " + anio);

            if (imagenPath != null && !imagenPath.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagenPath.replace("file://", ""));
                imageCarro.setImageBitmap(bitmap);
            }
        }

        cursor.close();
        db.close();
    }
}
