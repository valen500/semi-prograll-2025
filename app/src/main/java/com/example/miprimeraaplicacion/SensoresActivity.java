package com.example.miprimeraaplicacion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SensoresActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView txtAcelerometro, txtGiroscopio, txtLuz, txtProximidad;
    private Sensor acelerometro, giroscopio, luz, proximidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores); // Asegúrate de tener este layout

        txtAcelerometro = findViewById(R.id.txtAcelerometro);
        txtGiroscopio = findViewById(R.id.txtGiroscopio);
        txtLuz = findViewById(R.id.txtLuz);
        txtProximidad = findViewById(R.id.txtProximidad);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        luz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null)
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        if (giroscopio != null)
            sensorManager.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            txtAcelerometro.setText("Acelerómetro:\nX: " + event.values[0] +
                    "\nY: " + event.values[1] +
                    "\nZ: " + event.values[2]);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            txtGiroscopio.setText("Giroscopio:\nX: " + event.values[0] +
                    "\nY: " + event.values[1] +
                    "\nZ: " + event.values[2]);
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            txtLuz.setText("Luz: " + event.values[0] + " lx");
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            txtProximidad.setText("Proximidad: " + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementarlo ahora
    }
}
