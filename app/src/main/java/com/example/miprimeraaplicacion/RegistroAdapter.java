package com.example.miprimeraaplicacion;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

public class RegistroAdapter extends ArrayAdapter<Registro> {

    private Context context;
    private List<Registro> registros;

    public RegistroAdapter(@NonNull Context context, @NonNull List<Registro> registros) {
        super(context, 0, registros);
        this.context = context;
        this.registros = registros;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.registro_item, parent, false);
        }

        Registro registro = registros.get(position);

        TextView tvFecha = itemView.findViewById(R.id.tvFecha);
        TextView tvPeso = itemView.findViewById(R.id.tvPeso);
        ImageView ivImagen = itemView.findViewById(R.id.ivImagen);
        TextView tvAudio = itemView.findViewById(R.id.tvAudio);

        tvFecha.setText("Fecha: " + registro.getFecha());
        tvPeso.setText("Peso: " + registro.getPeso());

        // Mostrar imagen si está disponible
        if (registro.getImagenUri() != null) {
            ivImagen.setImageURI(Uri.parse(registro.getImagenUri()));
        } else {
            ivImagen.setImageResource(R.drawable.descarga); // Imagen por defecto
        }

        //  reproducción de audio si está disponible
        if (registro.getAudioPath() != null) {
            tvAudio.setText("Reproducir Audio");
            tvAudio.setOnClickListener(v -> {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(registro.getAudioPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvAudio.setText("Sin audio");
            tvAudio.setOnClickListener(null);
        }

        return itemView;
    }
}
