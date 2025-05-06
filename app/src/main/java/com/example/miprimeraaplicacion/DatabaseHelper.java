package com.example.miprimeraaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "salud.db";
    public static final int DB_VERSION = 1;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS salud");
        onCreate(db);
    }

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
}
