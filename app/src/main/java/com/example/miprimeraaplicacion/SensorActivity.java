package com.example.miprimeraaplicacion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro, luz, proximidad;
    private TextView pasosText, luzText, proximidadText, gpsText;
    private Button btnReiniciar, btnDetener;
    private LocationManager locationManager;

    private int contadorPasos = 0;
    private float aceleracionPrevia = 0;
    private float aceleracionActual = 0;
    private float delta = 0;
    private static final float UMBRAL_SALTO = 6.0f;

    private boolean contadorActivo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        pasosText = findViewById(R.id.pasosText);
        luzText = findViewById(R.id.luzText);
        proximidadText = findViewById(R.id.proximidadText);
        gpsText = findViewById(R.id.gpsText);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        btnDetener = findViewById(R.id.btnDetener);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        luz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }

        btnReiniciar.setOnClickListener(v -> {
            contadorPasos = 0;
            pasosText.setText("Pasos: 0");
            contadorActivo = true;
        });

        btnDetener.setOnClickListener(v -> {
            contadorActivo = false;
        });
    }

    private void startLocationUpdates() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpsText.setText("GPS:\nLat: " + location.getLatitude() + "\nLon: " + location.getLongitude());
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(String provider) {}
            @Override public void onProviderDisabled(String provider) {
                gpsText.setText("GPS desactivado");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null)
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI);
        if (luz != null)
            sensorManager.registerListener(this, luz, SensorManager.SENSOR_DELAY_NORMAL);
        if (proximidad != null)
            sensorManager.registerListener(this, proximidad, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!contadorActivo) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            aceleracionPrevia = aceleracionActual;
            aceleracionActual = (float) Math.sqrt(x * x + y * y + z * z);
            delta = aceleracionActual - aceleracionPrevia;

            if (delta > UMBRAL_SALTO) {
                contadorPasos++;
                pasosText.setText("Pasos: " + contadorPasos);
            }
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            luzText.setText("Sensor de Luz: " + event.values[0]);
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximidadText.setText("Proximidad: " + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
