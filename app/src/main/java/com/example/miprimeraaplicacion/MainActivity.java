package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView tempVal;
    RadioGroup rgo;

    RadioButton opt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btnCalcular);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempVal = findViewById(R.id.txtNum1);
                double num1 = Double.parseDouble(tempVal.getText().toString());

                tempVal = findViewById(R.id.txtNum2);
                double num2 = Double.parseDouble(tempVal.getText().toString());

                double respuesta = 0.0;

                opt = findViewById(R.id.optsuma);
                if (opt.isChecked()) {
                    respuesta = num1 + num2;
                }
                opt = findViewById(R.id.optresta);
                if (opt.isChecked()) {
                    respuesta = num1 - num2;
                }
                opt = findViewById(R.id.optmultiplicacion);
                if (opt.isChecked()) {
                    respuesta = num1 * num2;
                }
                opt = findViewById(R.id.optdivision);
                if (opt.isChecked()) {
                    respuesta = num1 / num2;
                }
                tempVal = findViewById(R.id.lblRespuesta);
                tempVal.setText("Respuesta: " + respuesta);

            }
        });
    }
}