package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView lblRespuesta;
    Spinner spnOpciones;
    EditText txtNum1, txtNum2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //   vistas
        btn = findViewById(R.id.btnCalcular);
        txtNum1 = findViewById(R.id.txtNum1);
        txtNum2 = findViewById(R.id.txtNum2);
        lblRespuesta = findViewById(R.id.lblRespuesta);
        spnOpciones = findViewById(R.id.spnOpciones);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "";
                try {
                    // Obtener valores numéricos
                    double num1 = txtNum1.getText().toString().isEmpty() ? 0 : Double.parseDouble(txtNum1.getText().toString());
                    double num2 = txtNum2.getText().toString().isEmpty() ? 0 : Double.parseDouble(txtNum2.getText().toString());
                    double resultado = 0.0;

                    //  operación según el Spinner
                    switch (spnOpciones.getSelectedItemPosition()) {
                        case 0: // Suma
                            resultado = num1 + num2;
                            msg = "La suma es: " + resultado;
                            break;
                        case 1: // Resta
                            resultado = num1 - num2;
                            msg = "La resta es: " + resultado;
                            break;
                        case 2: // Multiplicación
                            resultado = num1 * num2;
                            msg = "La multiplicacion es: " + resultado;
                            break;
                        case 3: // División
                            if (num2 == 0) {
                                lblRespuesta.setText("Error: División por cero.");
                                msg = "Error: División por cero.";
                                break;
                            }
                            resultado = num1 / num2;
                            msg = "La division es: " + resultado;
                            break;
                        case 4: // Exponente
                            resultado = Math.pow(num1, num2);
                            msg = "El exponente es: " + resultado;
                            break;
                        case 5: // Porcentaje
                            resultado = (num1 * num2) / 100;
                            msg = "El porcentaje es: " + resultado;
                            break;
                        case 6: // Raíz cuadrada
                            if (num1 < 0) {
                                lblRespuesta.setText("Error: No se puede calcular la raíz de un número negativo.");
                                msg = "Error: No se puede calcular la raíz de un número negativo.";
                                break;
                            }
                            resultado = Math.sqrt(num1);
                            msg = "La raiz cuadrada es: " + resultado;
                            break;
                        case 7: // Factorial
                            if (num1 < 0 || num1 != (int) num1) {
                                lblRespuesta.setText("Error: Factorial solo se aplica a enteros positivos.");
                                msg = "Error: Factorial solo se aplica a enteros positivos.";
                                break;
                            }
                            int n = (int) num1;
                            resultado = 1;
                            for (int i = 1; i <= n; i++) {
                                resultado *= i;
                            }
                            msg = "El factorial es: " + resultado;
                            break;
                        default:
                            lblRespuesta.setText("Error: Operación no válida.");
                            msg = "Error: Operación no válida.";
                            break;
                    }

                    // Mostrar el resultado
                    lblRespuesta.setText("Respuesta: " + resultado);
                    // Mostrar el mensaje
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

                } catch (NumberFormatException e) {
                    lblRespuesta.setText("Error: Ingresa números válidos.");
                    msg = "Error: Ingresa números válidos."; //
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
