package com.example.miprimeraaplicacion;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText etMensaje;
    private Button btnEnviar;
    private LinearLayout layoutMensajes;
    private ScrollView scrollView;
    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicializar vistas
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        layoutMensajes = findViewById(R.id.layoutMensajes);
        scrollView = findViewById(R.id.scrollView);

        // Configurar Firebase
        chatRef = FirebaseDatabase.getInstance().getReference("chatMensajes");

        // Configurar botón enviar
        btnEnviar.setOnClickListener(v -> enviarMensaje());

        // Cargar mensajes
        mostrarMensajes();
    }

    private void enviarMensaje() {
        String mensaje = etMensaje.getText().toString().trim();
        if (mensaje.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje primero", Toast.LENGTH_SHORT).show();
            return;
        }

        String autor = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        long timestamp = System.currentTimeMillis();

        Map<String, Object> mapMensaje = new HashMap<>();
        mapMensaje.put("autor", autor);
        mapMensaje.put("mensaje", mensaje);
        mapMensaje.put("timestamp", timestamp);

        chatRef.push().setValue(mapMensaje);
        etMensaje.setText("");
    }

    private void mostrarMensajes() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                String autor = snapshot.child("autor").getValue(String.class);
                String mensaje = snapshot.child("mensaje").getValue(String.class);
                Long time = snapshot.child("timestamp").getValue(Long.class);

                if (autor != null && mensaje != null && time != null) {
                    String fecha = DateFormat.format("dd MMM yyyy (HH:mm)", new Date(time)).toString();
                    boolean esMio = autor.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    agregarMensajeALayout(autor, mensaje, fecha, esMio);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarMensajeALayout(String autor, String mensaje, String fecha, boolean esMio) {
        //  contenedor para el mensaje
        LinearLayout mensajeLayout = new LinearLayout(this);
        mensajeLayout.setOrientation(LinearLayout.VERTICAL);

        //  márgenes
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 4, 0, 16);
        mensajeLayout.setLayoutParams(layoutParams);
        mensajeLayout.setGravity(esMio ? Gravity.END : Gravity.START);

        // TextView para el autor y fecha
        TextView tvAutorFecha = new TextView(this);
        tvAutorFecha.setText(autor + " - " + fecha);
        tvAutorFecha.setTextSize(12);
        tvAutorFecha.setTextColor(Color.parseColor("#757575"));
        tvAutorFecha.setPadding(16, 4, 16, 4);

        // TextView para el mensaje
        TextView tvMensaje = new TextView(this);
        tvMensaje.setText(mensaje);
        tvMensaje.setTextSize(16);
        tvMensaje.setTextColor(Color.parseColor("#333333"));
        tvMensaje.setPadding(16, 12, 16, 12);

        // Estilo según si es propio o no
        if (esMio) {
            tvMensaje.setBackgroundResource(R.drawable.bg_mensaje_propio);
        } else {
            tvMensaje.setBackgroundResource(R.drawable.bg_mensaje_recibido);
        }

        // Agregar vistas al layout
        mensajeLayout.addView(tvAutorFecha);
        mensajeLayout.addView(tvMensaje);

        // Agregar al layout principal
        layoutMensajes.addView(mensajeLayout);


        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}