package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class RegistroFisicoActivity extends AppCompatActivity {

    private static final String TAG = "RegistroFisicoActivity";
    private ListView listViewRegistros;
    private RegistroAdapter adapter;
    private List<Registro> listaRegistros;
    private DatabaseHelper databaseHelper;
    private DatabaseReference firebaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_fisico);

        listViewRegistros = findViewById(R.id.listViewRegistros);
        listaRegistros = new ArrayList<>();
        adapter = new RegistroAdapter(this, listaRegistros);
        listViewRegistros.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(this);
        firebaseReference = FirebaseDatabase.getInstance().getReference("registros");

        cargarDatosSQLite();
        cargarDatosFirebase();
    }

    private void cargarDatosSQLite() {
        List<Registro> registros = databaseHelper.obtenerRegistrosFisicos();

        if (registros != null && !registros.isEmpty()) {
            listaRegistros.clear();
            listaRegistros.addAll(registros);
            adapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "No hay registros en SQLite");
        }
    }

    private void cargarDatosFirebase() {
        firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Registro reg = ds.getValue(Registro.class);
                        if (reg != null) {
                            listaRegistros.add(reg);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "No hay registros en Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error al leer Firebase: " + error.getMessage());
                Toast.makeText(RegistroFisicoActivity.this, "Error al leer Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
