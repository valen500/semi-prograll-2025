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


        TextView tvCosto = view.findViewById(R.id.tvCosto);
        TextView tvGanancia = view.findViewById(R.id.tvGanancia);
        TextView tvStock = view.findViewById(R.id.tvStock);


        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo"));
        String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
        String presentacion = cursor.getString(cursor.getColumnIndexOrThrow("presentacion"));
        String marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"));
        double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
        double costo = cursor.getDouble(cursor.getColumnIndexOrThrow("costo"));
        double ganancia = cursor.getDouble(cursor.getColumnIndexOrThrow("ganancia"));
        int stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));
        String foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"));

        // Mostrar datos
        txtCodigo.setText("Código: " + codigo);
        txtDescripcion.setText("Descripción: " + descripcion);
        txtPresentacion.setText("Presentación: " + presentacion);
        txtPrecio.setText("Precio: $" + String.format("%.2f", precio));
        imgProducto.setImageResource(R.drawable.logo); // Puedes personalizar esto si usas imágenes reales


        tvCosto.setText("Costo: $" + String.format("%.2f", costo));
        tvGanancia.setText("Ganancia: $" + String.format("%.2f", ganancia));
        tvStock.setText("Stock: " + stock);


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
                    editProduct(productId);
                    return true;
                } else if (item.getItemId() == R.id.item_delete) {
                    deleteProduct(productId);
                    return true;
                } else if (item.getItemId() == R.id.item_add) {
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
                // Si deseas pasar más datos aquí, agrégalos igual

                mContext.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error al iniciar actividad de edición", e);
                Toast.makeText(mContext, "Error al editar producto", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(mContext, "Producto no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct(int productId) {
        if (db.deleteProduct(productId)) {
            changeCursor(db.getAllProducts());
            Toast.makeText(mContext, "Producto eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Error al eliminar producto", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarNuevoProducto() {
        try {
            Intent intent = new Intent(mContext, AddProductActivity.class);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar actividad de agregar producto", e);
            Toast.makeText(mContext, "Error al abrir pantalla de agregar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void changeCursor(Cursor newCursor) {
        super.changeCursor(newCursor);
    }
}
