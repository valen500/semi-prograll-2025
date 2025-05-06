package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenuActivity extends AppCompatActivity {

    CardView cardRegistroSalud, cardSensores, cardMultimedia, cardChat, cardLogout, cardVerRegistros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        cardRegistroSalud = findViewById(R.id.cardRegistroSalud);
        cardSensores = findViewById(R.id.cardSensores);
        cardMultimedia = findViewById(R.id.cardMultimedia);
        cardChat = findViewById(R.id.cardChat);
        cardLogout = findViewById(R.id.cardLogout);
        cardVerRegistros = findViewById(R.id.cardVerRegistros); // <- Nuevo

        cardRegistroSalud.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroSaludActivity.class)));

        cardSensores.setOnClickListener(v ->
                startActivity(new Intent(this, SensorActivity.class)));

        cardMultimedia.setOnClickListener(v ->
                startActivity(new Intent(this, MultimediaActivity.class)));

        cardChat.setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));

        cardLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        cardVerRegistros.setOnClickListener(v ->
                startActivity(new Intent(this, VerRegistrosActivity.class))); // <- Nuevo
    }
}
