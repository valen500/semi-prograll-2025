package com.example.miprimeraaplicacion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VerRegistrosActivity extends AppCompatActivity {

    ListView listaRegistros;
    ArrayList<String> lista;
    ArrayAdapter<String> adapter;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_registros);

        listaRegistros = findViewById(R.id.listaRegistros);
        lista = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listaRegistros.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);

        if (hayConexionInternet()) {
            cargarDesdeFirebase();
        } else {
            cargarDesdeSQLite();
        }
    }

    private boolean hayConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void cargarDesdeFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("registros").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot registroSnap : snapshot.getChildren()) {
                        Map<String, Object> datos = (Map<String, Object>) registroSnap.getValue();

                        String fecha = String.valueOf(datos.get("fecha"));
                        String saludable = String.valueOf(datos.get("saludable"));
                        String ejercicio = String.valueOf(datos.get("ejercicio_spinner"));
                        String agua = String.valueOf(datos.get("agua"));
                        String emocion = String.valueOf(datos.get("emocion"));

                        lista.add("📅 " + fecha +
                                "\n🍽 Saludable: " + saludable +
                                "\n🏃 Ejercicio: " + ejercicio +
                                "\n💧 Agua: " + agua +
                                "\n🙂 Emoción: " + emocion);
                    }
                } else {
                    lista.add("No hay registros en la nube.");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VerRegistrosActivity.this, "Error Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDesdeSQLite() {
        lista.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM salud ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String saludable = cursor.getString(cursor.getColumnIndexOrThrow("saludable"));
                String ejercicio = cursor.getString(cursor.getColumnIndexOrThrow("ejercicio_spinner"));
                String agua = cursor.getString(cursor.getColumnIndexOrThrow("agua"));
                String emocion = cursor.getString(cursor.getColumnIndexOrThrow("emocion"));

                lista.add("📅 " + fecha +
                        "\n🍽 Saludable: " + saludable +
                        "\n🏃 Ejercicio: " + ejercicio +
                        "\n💧 Agua: " + agua +
                        "\n🙂 Emoción: " + emocion);
            } while (cursor.moveToNext());
        } else {
            lista.add("No hay registros locales.");
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
