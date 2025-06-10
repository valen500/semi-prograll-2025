package com.example.miprimeraaplicacion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RegistroAdapter extends ArrayAdapter<Registro> {

    private final Context context;
    private final List<Registro> registros;

    public RegistroAdapter(Context context, List<Registro> registros) {
        super(context, 0, registros);
        this.context = context;
        this.registros = registros;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.registro_item, parent, false);
        }

        Registro registro = registros.get(position);

        TextView txtFecha = convertView.findViewById(R.id.txtFecha);
        TextView txtPeso = convertView.findViewById(R.id.txtPeso);
        ImageView imgFoto = convertView.findViewById(R.id.imgFoto);
        ImageButton btnAudio = convertView.findViewById(R.id.btnReproducir);
        ImageButton btnEliminar = convertView.findViewById(R.id.btnEliminar);

        txtFecha.setText("Fecha: " + registro.getFecha());
        txtPeso.setText("Peso: " + registro.getPeso());

        // Cargar imagen desde URI
        String imagenUri = registro.getImagenUri();
        if (imagenUri != null && !imagenUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(imagenUri);
                if ("file".equals(uri.getScheme())) {
                    File imgFile = new File(uri.getPath());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imgFoto.setImageBitmap(bitmap);
                    } else {
                        Log.w("RegistroAdapter", "Archivo de imagen no encontrado: " + uri.getPath());
                        imgFoto.setImageResource(R.drawable.descarga);
                    }
                } else {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgFoto.setImageBitmap(bitmap);
                        inputStream.close();
                    } else {
                        imgFoto.setImageResource(R.drawable.descarga);
                    }
                }
            } catch (Exception e) {
                Log.e("RegistroAdapter", "Error al cargar imagen", e);
                imgFoto.setImageResource(R.drawable.descarga);
            }
        } else {
            imgFoto.setImageResource(R.drawable.descarga);
        }

        // Reproducir audio
        btnAudio.setOnClickListener(v -> {
            String audioPath = registro.getAudioPath();
            if (audioPath != null && !audioPath.isEmpty()) {
                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(audioPath);
                    player.prepare();
                    player.start();
                    Toast.makeText(context, "Reproduciendo audio...", Toast.LENGTH_SHORT).show();
                    player.setOnCompletionListener(mp -> {
                        mp.release();
                        Toast.makeText(context, "Reproducción finalizada", Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    Log.e("RegistroAdapter", "Error al reproducir audio", e);
                    Toast.makeText(context, "No se pudo reproducir el audio", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No hay audio disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // Eliminar registro físico
        btnEliminar.setOnClickListener(v -> {
            Registro registroAEliminar = registros.get(position);

            // 1. Eliminar de SQLite
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("registros_fisicos", "fecha=? AND peso=?", new String[]{
                    registroAEliminar.getFecha(),
                    registroAEliminar.getPeso()
            });
            db.close();

            // 2. Eliminar de Firebase (opcional)
            if (registroAEliminar.getId() != null && !registroAEliminar.getId().isEmpty()) {
                DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("registros");
                firebaseRef.child(registroAEliminar.getId()).removeValue();
            }

            // 3. Eliminar del adaptador
            registros.remove(position);
            notifyDataSetChanged();

            Toast.makeText(context, "Registro eliminado", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
