package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TabHost tbh;
    Button btn;
    EditText txtCantidad;
    TextView lblRespuesta;
    Spinner spnDe, spnA;
    Conversores objConversores = new Conversores();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbh = findViewById(R.id.tbhConversor);
        tbh.setup();
        tbh.addTab(tbh.newTabSpec("Monedas").setContent(R.id.tabMonedas).setIndicator("MONEDAS"));
        tbh.addTab(tbh.newTabSpec("Longitud").setContent(R.id.tabLongitud).setIndicator("LONGITUD"));
        tbh.addTab(tbh.newTabSpec("Tiempo").setContent(R.id.tabTiempo).setIndicator("TIEMPO"));
        tbh.addTab(tbh.newTabSpec("Almacenamiento").setContent(R.id.tabAlmacenamiento).setIndicator("ALMACENAMIENTO"));
        tbh.addTab(tbh.newTabSpec("Masa").setContent(R.id.tabMasa).setIndicator("MASA"));
        tbh.addTab(tbh.newTabSpec("Volumen").setContent(R.id.tabVolumen).setIndicator("VOLUMEN"));

        btn = findViewById(R.id.btnCalcular);
        txtCantidad = findViewById(R.id.txtCantidad);
        lblRespuesta = findViewById(R.id.lblRespuesta);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int opcion = tbh.getCurrentTab();

                switch (opcion) {
                    case 0:
                        spnDe = findViewById(R.id.spnDeMonedas);
                        spnA = findViewById(R.id.spnAMonedas);
                        break;
                    case 1:
                        spnDe = findViewById(R.id.spnDeLongitud);
                        spnA = findViewById(R.id.spnALongitud);
                        break;
                    case 2:
                        spnDe = findViewById(R.id.spnDeTiempo);
                        spnA = findViewById(R.id.spnATiempo);
                        break;
                    case 3:
                        spnDe = findViewById(R.id.spnDeAlmacenamiento);
                        spnA = findViewById(R.id.spnAAlmacenamiento);
                        break;
                    case 4:
                        spnDe = findViewById(R.id.spnDeMasa);
                        spnA = findViewById(R.id.spnAMasa);
                        break;
                    case 5:
                        spnDe = findViewById(R.id.spnDeVolumen);
                        spnA = findViewById(R.id.spnAVolumen);
                        break;
                    default:
                        return;
                }

                int de = spnDe.getSelectedItemPosition();
                int a = spnA.getSelectedItemPosition();

                try {
                    String cantidadStr = txtCantidad.getText().toString();
                    if (cantidadStr.isEmpty()) {
                        mostrarMensaje("Ingrese una cantidad");
                        return;
                    }

                    double cantidad = Double.parseDouble(cantidadStr);
                    double respuesta = objConversores.convertir(opcion, de, a, cantidad);
                    if (respuesta != -1) {
                        String mensaje = "Conversión realizada con éxito. Resultado: " + respuesta;
                        lblRespuesta.setText("Respuesta: " + respuesta);
                        mostrarMensaje(mensaje);
                    } else {
                        mostrarMensaje("Error en la conversión");
                    }

                } catch (NumberFormatException e) {
                    mostrarMensaje("Ingrese un valor numérico válido");
                }
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
    }
}

class Conversores {
    //  valores de los elementos por cada tipo de conversión
    double[][] valores = {
            // Monedas
            {1, 0.98, 7.73, 25.45, 36.78, 508.87, 8.74, 0.85, 1.21, 0.64},
            // Longitud
            {1, 0.001, 100, 39.37, 3.28, 1.094, 0.621371, 1000, 0.001, 1.09361},
            // Tiempo
            {1, 1.0/60, 1.0/3600, 1.0/86400, 1.0/604800, 1.0/2628000, 1.0/31536000, 1000, 0.001, 1.0/86400},
            // Almacenamiento
            {1, 1.0/1024, 1.0/1048576, 1.0/1073741824, 1000, 0.001, 0.000001, 1.0/1048576, 1.0/1073741824, 1000000},
            // Masa
            {1, 1000, 0.001, 35.274, 2.20462, 0.0283495, 0.000984207, 1000, 1, 0.00220462},
            // Volumen
            {1, 1000, 1000, 0.264172, 2.11338, 1000, 0.000264172, 0.0353147, 1, 0.0353147}
    };

    public double convertir(int opcion, int de, int a, double cantidad) {
        if (opcion >= valores.length || de >= valores[opcion].length || a >= valores[opcion].length || cantidad < 0) {
            return -1;
        }
        return valores[opcion][a] / valores[opcion][de] * cantidad;
    }
}
