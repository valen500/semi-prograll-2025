package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegistroSaludActivity extends AppCompatActivity {

    EditText fechaEditText, suenoEditText;
    CheckBox ejercicioCheckBox, comidaCheckBox;
    Spinner spinnerSaludable, spinnerEjercicio, spinnerAgua, spinnerEmocion;
    Button guardarButton;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_salud);

        fechaEditText = findViewById(R.id.fechaEditText);
        suenoEditText = findViewById(R.id.suenoEditText);
        ejercicioCheckBox = findViewById(R.id.ejercicioCheckBox);
        comidaCheckBox = findViewById(R.id.comidaCheckBox);
        spinnerSaludable = findViewById(R.id.spinnerSaludable);
        spinnerEjercicio = findViewById(R.id.spinnerEjercicio);
        spinnerAgua = findViewById(R.id.spinnerAgua);
        spinnerEmocion = findViewById(R.id.spinnerEmocion);
        guardarButton = findViewById(R.id.guardarButton);

        dbHelper = new DatabaseHelper(this);  // NUEVO

        guardarButton.setOnClickListener(v -> {
            try {
                String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                int sueno = Integer.parseInt(suenoEditText.getText().toString());
                boolean ejercicio = ejercicioCheckBox.isChecked();
                boolean comida = comidaCheckBox.isChecked();
                String saludable = spinnerSaludable.getSelectedItem().toString();
                String ejercicioStr = spinnerEjercicio.getSelectedItem().toString();
                String agua = spinnerAgua.getSelectedItem().toString();
                String emocion = spinnerEmocion.getSelectedItem().toString();

                // Guardar en Firebase
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("registros").child(uid);
                String key = ref.push().getKey();

                HashMap<String, Object> registroFirebase = new HashMap<>();
                registroFirebase.put("fecha", fecha);
                registroFirebase.put("sueno", sueno);
                registroFirebase.put("ejercicio_checkbox", ejercicio);
                registroFirebase.put("comida_checkbox", comida);
                registroFirebase.put("saludable", saludable);
                registroFirebase.put("ejercicio_spinner", ejercicioStr);
                registroFirebase.put("agua", agua);
                registroFirebase.put("emocion", emocion);

                ref.child(key).setValue(registroFirebase);

                // Guardar en SQLite
                boolean guardadoLocal = dbHelper.guardarDatos(
                        fecha, sueno, ejercicio, comida, saludable, ejercicioStr, agua, emocion
                );

                if (guardadoLocal) {
                    Toast.makeText(this, "Datos guardados (local y en nube)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al guardar localmente", Toast.LENGTH_SHORT).show();
                }

                // Limpiar campos
                fechaEditText.setText("");
                suenoEditText.setText("");
                ejercicioCheckBox.setChecked(false);
                comidaCheckBox.setChecked(false);
                spinnerSaludable.setSelection(0);
                spinnerEjercicio.setSelection(0);
                spinnerAgua.setSelection(0);
                spinnerEmocion.setSelection(0);

            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
