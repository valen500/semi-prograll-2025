package com.example.miprimeraaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CivCarStore.db";
    private static final int DATABASE_VERSION = 7; // Versión incrementada
    private static final String TABLE_PRODUCTS = "productos";

    // Columnas
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_CODIGO = "codigo";
    private static final String COLUMN_DESCRIPCION = "descripcion";
    private static final String COLUMN_PRESENTACION = "presentacion";
    private static final String COLUMN_MARCA = "marca";
    private static final String COLUMN_PRECIO = "precio";
    private static final String COLUMN_FOTO = "foto";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CODIGO + " TEXT NOT NULL, " +
                COLUMN_DESCRIPCION + " TEXT NOT NULL, " +
                COLUMN_PRESENTACION + " TEXT NOT NULL, " +
                COLUMN_MARCA + " TEXT NOT NULL, " +
                COLUMN_PRECIO + " REAL NOT NULL, " +
                COLUMN_FOTO + " TEXT)";
        db.execSQL(createTable);

        //  datos de prueba
        insertTestData(db);
    }

    private void insertTestData(SQLiteDatabase db) {
        insertProduct(db, "001", "Aceite Motor", "1 Litro", "Mobil", 25.99, "default");
        insertProduct(db, "002", "Filtro Aire", "Universal", "Fram", 15.50, "default");
        insertProduct(db, "003", "Pastillas Freno", "Delantera", "Brembo", 45.75, "default");
    }

    private long insertProduct(SQLiteDatabase db, String codigo, String descripcion,
                               String presentacion, String marca, double precio, String foto) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CODIGO, codigo);
        values.put(COLUMN_DESCRIPCION, descripcion);
        values.put(COLUMN_PRESENTACION, presentacion);
        values.put(COLUMN_MARCA, marca);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_FOTO, foto);
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public boolean insertProduct(String codigo, String descripcion, String presentacion,
                                 String marca, double precio, String foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = insertProduct(db, codigo, descripcion, presentacion, marca, precio, foto);
        db.close();
        return result != -1;
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PRODUCTS,
                null, null, null, null, null, null);
    }

    public Cursor getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PRODUCTS,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
    }

    public boolean updateProduct(int id, String codigo, String descripcion,
                                 String presentacion, String marca, double precio, String foto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CODIGO, codigo);
        values.put(COLUMN_DESCRIPCION, descripcion);
        values.put(COLUMN_PRESENTACION, presentacion);
        values.put(COLUMN_MARCA, marca);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_FOTO, foto);

        int rows = db.update(TABLE_PRODUCTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public Cursor searchProducts(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PRODUCTS,
                null,
                COLUMN_DESCRIPCION + " LIKE ? OR " + COLUMN_MARCA + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"},
                null, null, null);
    }
}