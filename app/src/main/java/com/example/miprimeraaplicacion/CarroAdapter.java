package com.example.miprimeraaplicacion;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

public class CarroAdapter extends RecyclerView.Adapter<CarroAdapter.ViewHolder> {

    private List<Carro> listaCarros;
    private OnItemClickListener listener;

    // Interfaz para manejar clics
    public interface OnItemClickListener {
        void onItemClick(Carro carro);
    }

    // Constructor actualizado
    public CarroAdapter(List<Carro> carros, OnItemClickListener listener) {
        this.listaCarros = carros;
        this.listener = listener;
    }

    @Override
    public CarroAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarroAdapter.ViewHolder holder, int position) {
        Carro carro = listaCarros.get(position);
        holder.tvMarcaModelo.setText(carro.getMarca() + " " + carro.getModelo());
        holder.tvAnio.setText("Año: " + carro.getAnio());

        // Mostrar imagen
        String uriImagen = carro.getImagen();
        if (uriImagen != null) {
            try {
                Uri imageUri = Uri.parse(uriImagen);
                ContentResolver resolver = holder.itemView.getContext().getContentResolver();
                InputStream inputStream = resolver.openInputStream(imageUri);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    holder.imgCarro.setImageBitmap(bitmap);
                    inputStream.close();
                } else {
                    holder.imgCarro.setImageResource(R.drawable.ic_launcher_background);
                }
            } catch (Exception e) {
                Log.e("CarroAdapter", "Error al cargar imagen: " + e.getMessage());
                holder.imgCarro.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.imgCarro.setImageResource(R.drawable.ic_launcher_background);
        }

        // Configurar clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(carro);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCarros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMarcaModelo, tvAnio;
        ImageView imgCarro;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMarcaModelo = itemView.findViewById(R.id.tvMarcaModelo);
            tvAnio = itemView.findViewById(R.id.tvAnio);
            imgCarro = itemView.findViewById(R.id.imgCarro);
        }
    }
}
