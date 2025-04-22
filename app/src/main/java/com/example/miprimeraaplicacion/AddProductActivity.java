package com.example.miprimeraaplicacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private Uri imageUri;

    private EditText etCodigo, etDescripcion, etPresentacion, etMarca, etPrecio;
    private EditText etCosto, etGanancia, etStock;
    private Button btnGuardar;
    private ImageView imageView;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DB(this);

        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPresentacion = findViewById(R.id.etPresentacion);
        etMarca = findViewById(R.id.etMarca);
        etPrecio = findViewById(R.id.etPrecio);
        etCosto = findViewById(R.id.etCosto);
        etGanancia = findViewById(R.id.etGanancia);
        etStock = findViewById(R.id.etStock);

        btnGuardar = findViewById(R.id.btnGuardar);
        imageView = findViewById(R.id.imageView);

        Button btnAgregarImagen = findViewById(R.id.btnAgregarImagen);
        btnAgregarImagen.setOnClickListener(v -> showImageSourceDialog());

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void showImageSourceDialog() {
        CharSequence[] options = {"Tomar foto", "Elegir de la galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Tomar foto")) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        imageUri = FileProvider.getUriForFile(this,
                                "com.example.miprimeraaplicacion.fileprovider", photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            } else if (options[item].equals("Elegir de la galería")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri selectedImageUri = data.getData();
                imageUri = selectedImageUri;
                imageView.setImageURI(selectedImageUri);
            } else if (requestCode == CAMERA_REQUEST) {
                imageView.setImageURI(imageUri);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void guardarProducto() {
        String codigo = etCodigo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String presentacion = etPresentacion.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String costoStr = etCosto.getText().toString().trim();
        String gananciaStr = etGanancia.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (codigo.isEmpty() || descripcion.isEmpty() || presentacion.isEmpty() ||
                marca.isEmpty() || precioStr.isEmpty() || costoStr.isEmpty() ||
                gananciaStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            double costo = Double.parseDouble(costoStr);
            double ganancia = Double.parseDouble(gananciaStr);
            int stock = Integer.parseInt(stockStr);

            if (precio <= 0 || costo <= 0 || ganancia <= 0 || stock < 0) {
                Toast.makeText(this, "Los valores deben ser válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean resultado = db.insertProduct(codigo, descripcion, presentacion, marca, precio, "default_image", costo, ganancia, stock);

            if (resultado) {
                // También guardar en Firebase
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("productos");
                String key = database.push().getKey();

                Product producto = new Product(codigo, descripcion, presentacion, marca, precio, "default_image", costo, ganancia, stock);

                database.child(key).setValue(producto)
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Producto guardado en Firebase", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error en Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al guardar en SQLite", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }
}
