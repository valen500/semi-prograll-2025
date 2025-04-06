package com.example.miprimeraaplicacion;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class ProductAdapter extends CursorAdapter {
    private static final String TAG = "ProductAdapter";
    private final DB db;
    private final Context mContext;

    public ProductAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.mContext = context;
        this.db = new DB(context);
        Log.d(TAG, "Adapter inicializado");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "Creando nueva vista");
        return LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, "Configurando vista");


        TextView txtCodigo = view.findViewById(R.id.txtCodigo);
        TextView txtDescripcion = view.findViewById(R.id.txtDescripcion);
        TextView txtPresentacion = view.findViewById(R.id.txtPresentacion);
        TextView txtPrecio = view.findViewById(R.id.txtPrecio);
        ImageButton btnOptions = view.findViewById(R.id.btnOptions);
        ImageView imgProducto = view.findViewById(R.id.imgProducto);

        // Obtener datos del cursor
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo"));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
        String presentacion = cursor.getString(cursor.getColumnIndexOrThrow("presentacion"));
        String marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"));
        double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
        String foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"));

        // Mostrar datos
        txtCodigo.setText("Código: " + codigo);
        txtDescripcion.setText("Descripción: " + descripcion);
        txtPresentacion.setText("Presentación: " + presentacion);
        txtPrecio.setText("Precio: $" + String.format("%.2f", precio));
        imgProducto.setImageResource(R.drawable.logo);

        // Configurar botón de opciones
        btnOptions.setOnClickListener(v -> {
            Log.d(TAG, "Botón de opciones clickeado para el producto ID: " + id);
            showPopupMenu(v, id);
        });
    }

    private void showPopupMenu(View view, int productId) {
        Log.d(TAG, "Mostrando menú popup");

        PopupMenu popup = new PopupMenu(mContext, view);
        try {
            popup.inflate(R.menu.product_options_menu);

            popup.setOnMenuItemClickListener(item -> {
                Log.d(TAG, "Ítem del menú seleccionado: " + item.getTitle());

                if (item.getItemId() == R.id.item_edit) {
                    Log.d(TAG, "Editando producto ID: " + productId);
                    editProduct(productId);
                    return true;
                } else if (item.getItemId() == R.id.item_delete) {
                    Log.d(TAG, "Eliminando producto ID: " + productId);
                    deleteProduct(productId);
                    return true;
                } else if (item.getItemId() == R.id.item_add) {
                    Log.d(TAG, "Agregar nuevo producto seleccionado");
                    agregarNuevoProducto();
                    return true;
                }
                return false;
            });

            popup.setOnDismissListener(menu -> {
                Log.d(TAG, "Menú popup cerrado");
            });

            popup.show();
        } catch (Exception e) {
            Log.e(TAG, "Error al mostrar menú popup", e);
            Toast.makeText(mContext, "Error al mostrar opciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void editProduct(int productId) {
        Log.d(TAG, "Iniciando edición para el producto ID: " + productId);

        Cursor cursor = db.getProductById(productId);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                Intent intent = new Intent(mContext, EditProductActivity.class);
                intent.putExtra("id", productId);
                intent.putExtra("codigo", cursor.getString(cursor.getColumnIndexOrThrow("codigo")));
                intent.putExtra("descripcion", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
                intent.putExtra("presentacion", cursor.getString(cursor.getColumnIndexOrThrow("presentacion")));
                intent.putExtra("marca", cursor.getString(cursor.getColumnIndexOrThrow("marca")));
                intent.putExtra("precio", cursor.getDouble(cursor.getColumnIndexOrThrow("precio")));
                intent.putExtra("foto", cursor.getString(cursor.getColumnIndexOrThrow("foto")));

                mContext.startActivity(intent);
                Log.d(TAG, "Actividad de edición iniciada");
            } catch (Exception e) {
                Log.e(TAG, "Error al iniciar actividad de edición", e);
                Toast.makeText(mContext, "Error al editar producto", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        } else {
            Log.w(TAG, "No se encontró el producto con ID: " + productId);
            Toast.makeText(mContext, "Producto no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct(int productId) {
        Log.d(TAG, "Eliminando producto ID: " + productId);

        if (db.deleteProduct(productId)) {
            Log.d(TAG, "Producto eliminado exitosamente");
            changeCursor(db.getAllProducts());
            Toast.makeText(mContext, "Producto eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "Error al eliminar producto");
            Toast.makeText(mContext, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarNuevoProducto() {
        Log.d(TAG, "Iniciando actividad para agregar nuevo producto");

        try {
            Intent intent = new Intent(mContext, AddProductActivity.class);
            mContext.startActivity(intent);
            Log.d(TAG, "Actividad de agregar producto iniciada");
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar actividad de agregar producto", e);
            Toast.makeText(mContext, "Error al abrir pantalla de agregar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void changeCursor(Cursor newCursor) {
        Log.d(TAG, "Cambiando cursor del adaptador");
        super.changeCursor(newCursor);
    }
}