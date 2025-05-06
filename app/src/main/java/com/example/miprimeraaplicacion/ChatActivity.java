package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    EditText etMensaje;
    Button btnEnviar;
    ListView listaMensajes;
    ArrayAdapter<String> adapter;
    ArrayList<String> mensajes;

    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        listaMensajes = findViewById(R.id.listaMensajes);

        mensajes = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mensajes);
        listaMensajes.setAdapter(adapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("mensajes");

        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                databaseRef.push().setValue(texto);
                etMensaje.setText("");
            }
        });

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mensajes.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String msg = snap.getValue(String.class);
                    mensajes.add(msg);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
