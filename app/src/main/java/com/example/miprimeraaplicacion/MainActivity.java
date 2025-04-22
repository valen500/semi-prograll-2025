package com.example.miprimeraaplicacion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DB db;
    private ListView listView;
    private ProductAdapter adapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DB(this);
        listView = findViewById(R.id.listViewProductos);

        loadProducts();
    }

    public void loadProducts() {
        if (cursor != null) {
            cursor.close();
        }

        cursor = db.getAllProducts();
        if (cursor != null && cursor.getCount() > 0) {
            adapter = new ProductAdapter(this, cursor);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No hay productos disponibles", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_add) {

            startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }
}