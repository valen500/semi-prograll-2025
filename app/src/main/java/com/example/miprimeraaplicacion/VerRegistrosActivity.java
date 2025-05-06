package com.example.miprimeraaplicacion;

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

public class VerRegistrosActivity extends AppCompatActivity {

    ListView listaRegistros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_registros);

        listaRegistros = findViewById(R.id.listaRegistros);
        ArrayList<String> lista = new ArrayList<>();

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
                    lista.add("No hay registros disponibles.");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(VerRegistrosActivity.this, android.R.layout.simple_list_item_1, lista);
                listaRegistros.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VerRegistrosActivity.this, "Error al leer datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
