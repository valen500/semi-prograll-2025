package com.example.miprimeraaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "salud.db";
    public static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE salud (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha TEXT, " +
                "sueno INTEGER, " +
                "ejercicio_check INTEGER, " +
                "comida_check INTEGER, " +
                "saludable TEXT, " +
                "ejercicio_spinner TEXT, " +
                "agua TEXT, " +
                "emocion TEXT)");

        db.execSQL("CREATE TABLE registros_fisicos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha TEXT, " +
                "peso TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE registros_fisicos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "fecha TEXT, " +
                    "peso TEXT)");
        }
    }

    // Guarda registros de salud
    public boolean guardarDatos(String fecha, int sueno, boolean ejercicioCheck, boolean comidaCheck,
                                String saludable, String ejercicioSpinner, String agua, String emocion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha", fecha);
        values.put("sueno", sueno);
        values.put("ejercicio_check", ejercicioCheck ? 1 : 0);
        values.put("comida_check", comidaCheck ? 1 : 0);
        values.put("saludable", saludable);
        values.put("ejercicio_spinner", ejercicioSpinner);
        values.put("agua", agua);
        values.put("emocion", emocion);

        long result = db.insert("salud", null, values);
        return result != -1;
    }

    // Guarda registros físicos simples (fecha y peso)
    public boolean guardarRegistroFisico(String fecha, String peso) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha", fecha);
        values.put("peso", peso);

        long result = db.insert("registros_fisicos", null, values);
        return result != -1;
    }

    // Devuelve registros físicos  de la clase Registro
    public List<Registro> obtenerRegistrosFisicos() {
        List<Registro> registros = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, fecha, peso FROM registros_fisicos ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            String id = String.valueOf(cursor.getInt(0));
            String fecha = cursor.getString(1);
            String peso = cursor.getString(2);
            registros.add(new Registro(id, fecha, peso));
        }

        cursor.close();
        db.close();
        return registros;
    }
}
