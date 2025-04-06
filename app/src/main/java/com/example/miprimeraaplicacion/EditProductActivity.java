package com.example.miprimeraaplicacion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProductActivity extends AppCompatActivity {
    private EditText etCodigo, etDescripcion, etPresentacion, etMarca, etPrecio;
    private Button btnEditar;
    private DB db;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        db = new DB(this);
        productId = getIntent().getIntExtra("id", -1);

        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPresentacion = findViewById(R.id.etPresentacion);
        etMarca = findViewById(R.id.etMarca);
        etPrecio = findViewById(R.id.etPrecio);
        btnEditar = findViewById(R.id.btneditar); // CORREGIDO: btneditar

        cargarDatosProducto();
        btnEditar.setOnClickListener(v -> actualizarProducto());
    }

    private void cargarDatosProducto() {
        etCodigo.setText(getIntent().getStringExtra("codigo"));
        etDescripcion.setText(getIntent().getStringExtra("descripcion"));
        etPresentacion.setText(getIntent().getStringExtra("presentacion"));
        etMarca.setText(getIntent().getStringExtra("marca"));
        etPrecio.setText(String.valueOf(getIntent().getDoubleExtra("precio", 0)));
    }

    private void actualizarProducto() {
        String codigo = etCodigo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String presentacion = etPresentacion.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        if (codigo.isEmpty() || descripcion.isEmpty() || presentacion.isEmpty() || marca.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            boolean updated = db.updateProduct(productId, codigo, descripcion, presentacion, marca, precio, "default_image");

            if (updated) {
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar producto", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
