package com.example.miprimeraaplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private Uri imageUri;

    private EditText etCodigo, etDescripcion, etPresentacion, etMarca, etPrecio;
    private Button btnGuardar;
    private ImageView imageView;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = new DB(this);

        // Inicializar vistas
        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPresentacion = findViewById(R.id.etPresentacion);
        etMarca = findViewById(R.id.etMarca);
        etPrecio = findViewById(R.id.etPrecio);
        btnGuardar = findViewById(R.id.btnGuardar);
        imageView = findViewById(R.id.imageView);

        // Configurar listener del botón para agregar imagen
        Button btnAgregarImagen = findViewById(R.id.btnAgregarImagen);
        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarProducto();
            }
        });
    }

    private void showImageSourceDialog() {

        CharSequence[] options = new CharSequence[]{"Tomar foto", "Elegir de la galería"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductActivity.this);
        builder.setTitle("Selecciona una opción");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tomar foto")) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Verifica si la cámara está disponible
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (photoFile != null) {
                            imageUri = FileProvider.getUriForFile(AddProductActivity.this,
                                    "com.example.miprimeraaplicacion.fileprovider", photoFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                } else if (options[item].equals("Elegir de la galería")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
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
                imageView.setImageURI(selectedImageUri);
            } else if (requestCode == CAMERA_REQUEST) {
                imageView.setImageURI(imageUri);
            }
        }
    }


    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }


    private void guardarProducto() {
        String codigo = etCodigo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String presentacion = etPresentacion.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        // Validación de campos
        if (codigo.isEmpty() || descripcion.isEmpty() || presentacion.isEmpty() || marca.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                Toast.makeText(this, "El precio debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insertar producto en la base de datos
            boolean resultado = db.insertProduct(codigo, descripcion, presentacion, marca, precio, "default_image");

            if (resultado) {
                Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al agregar producto", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un precio válido", Toast.LENGTH_SHORT).show();
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
