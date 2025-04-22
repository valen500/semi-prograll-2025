package com.example.miprimeraaplicacion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CarroDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "carros.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CARRO = "carro";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MARCA = "marca";
    public static final String COLUMN_MODELO = "modelo";
    public static final String COLUMN_ANIO = "anio";
    public static final String COLUMN_IMAGEN = "imagen";
    public static final String COLUMN_AUDIO = "audio"; // ✅ nueva columna

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CARRO + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MARCA + " TEXT, " +
                    COLUMN_MODELO + " TEXT, " +
                    COLUMN_ANIO + " INTEGER, " +
                    COLUMN_IMAGEN + " TEXT, " +
                    COLUMN_AUDIO + " TEXT);"; // ✅ nueva columna en la tabla

    public CarroDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí podrías manejar migraciones si subes DATABASE_VERSION
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARRO);
        onCreate(db);
    }
}
