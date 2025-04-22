package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerCarros;
    private CarroAdapter carroAdapter;
    private List<Carro> listaCarros;
    private CarroDbHelper dbHelper;
    private Button btnAgregar;
    private Button btnSensores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerCarros = findViewById(R.id.recyclerCarros);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnSensores = findViewById(R.id.btnSensores);
        dbHelper = new CarroDbHelper(this);

        recyclerCarros.setLayoutManager(new LinearLayoutManager(this));

        btnAgregar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AgregarCarroActivity.class);
            startActivity(intent);
        });

        btnSensores.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SensoresActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCarros();
    }

    private void cargarCarros() {
        listaCarros = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CarroDbHelper.TABLE_CARRO,
                null, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            Carro carro = new Carro();
            carro.setId(cursor.getInt(cursor.getColumnIndex(CarroDbHelper.COLUMN_ID)));
            carro.setMarca(cursor.getString(cursor.getColumnIndex(CarroDbHelper.COLUMN_MARCA)));
            carro.setModelo(cursor.getString(cursor.getColumnIndex(CarroDbHelper.COLUMN_MODELO)));
            carro.setAnio(cursor.getInt(cursor.getColumnIndex(CarroDbHelper.COLUMN_ANIO)));
            carro.setImagen(cursor.getString(cursor.getColumnIndex("imagen")));
            listaCarros.add(carro);
        }
        cursor.close();

        carroAdapter = new CarroAdapter(listaCarros, carro -> {
            Intent intent = new Intent(MainActivity.this, DetalleCarroActivity.class);
            intent.putExtra("idCarro", carro.getId());
            startActivity(intent);
        });

        recyclerCarros.setAdapter(carroAdapter);
    }
}
