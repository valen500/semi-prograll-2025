package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputMetros, inputValor;
    private TextView txtResultadoAgua, txtResultadoConversion;
    private Spinner spDe, spA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Agua");
        spec1.setIndicator("Tarifa de Agua");
        spec1.setContent(R.id.tab1);
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Conversor");
        spec2.setIndicator("Conversor de Área");
        spec2.setContent(R.id.tab2);
        tabHost.addTab(spec2);

        inputMetros = findViewById(R.id.inputMetros);
        txtResultadoAgua = findViewById(R.id.txtResultadoAgua);
        Button btnCalcularAgua = findViewById(R.id.btnCalcularAgua);

        btnCalcularAgua.setOnClickListener(v -> calcularTarifaAgua());

        inputValor = findViewById(R.id.inputValor);
        spDe = findViewById(R.id.spDe);
        spA = findViewById(R.id.spA);
        txtResultadoConversion = findViewById(R.id.txtResultadoConversion);
        Button btnConvertir = findViewById(R.id.btnConvertir);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.unidades_area, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDe.setAdapter(adapter);
        spA.setAdapter(adapter);

        btnConvertir.setOnClickListener(v -> convertirArea());
    }

    private void calcularTarifaAgua() {
        String inputText = inputMetros.getText().toString();
        if (inputText.isEmpty()) {
            txtResultadoAgua.setText("Ingrese un valor válido");
            return;
        }

        int metros = Integer.parseInt(inputText);
        double totalPagar;

        if (metros <= 18) {
            totalPagar = 6.00;
        } else if (metros <= 28) {
            totalPagar = 6.00 + (metros - 18) * 0.45;
        } else {
            totalPagar = 6.00 + (10 * 0.45) + (metros - 28) * 0.65;
        }

        txtResultadoAgua.setText("Valor a Pagar: $" + String.format("%.2f", totalPagar));
    }

    private void convertirArea() {
        String inputText = inputValor.getText().toString();
        if (inputText.isEmpty()) {
            txtResultadoConversion.setText("Ingrese un valor válido");
            return;
        }

        double valor = Double.parseDouble(inputText);
        String unidadDe = spDe.getSelectedItem().toString();
        String unidadA = spA.getSelectedItem().toString();
        double resultado = convertirUnidad(valor, unidadDe, unidadA);

        txtResultadoConversion.setText("Resultado: " + resultado + " " + unidadA);
    }

    private double convertirUnidad(double valor, String de, String a) {
        double metroCuadrado = 0;

        switch (de) {
            case "Pie Cuadrado":
                metroCuadrado = valor * 0.093;
                break;
            case "Vara Cuadrada":
                metroCuadrado = valor * 0.6984;
                break;
            case "Yarda Cuadrada":
                metroCuadrado = valor * 0.8361;
                break;
            case "Tarea":
                metroCuadrado = valor * 16;
                break;
            case "Manzana":
                metroCuadrado = valor * 7000;
                break;
            case "Hectárea":
                metroCuadrado = valor * 10000;
                break;
            default:
                metroCuadrado = valor;
                break;
        }

        double resultado = 0;

        switch (a) {
            case "Pie Cuadrado":
                resultado = metroCuadrado / 0.093;
                break;
            case "Vara Cuadrada":
                resultado = metroCuadrado / 0.6988;
                break;
            case "Yarda Cuadrada":
                resultado = metroCuadrado / 0.8361;
                break;
            case "Tarea":
                resultado = metroCuadrado / 6988;
                break;
            case "Manzana":
                resultado = metroCuadrado / 111808;
                break;
            case "Hectárea":
                resultado = metroCuadrado / 10000;
                break;
            default:
                resultado = metroCuadrado;
                break;
        }

        return resultado;
    }
}
