package com.example.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<String> forecastData;

    public ForecastAdapter(List<String> forecastData) {
        if (forecastData == null) {
            this.forecastData = new ArrayList<>();
        } else {
            this.forecastData = forecastData;
        }
    }

    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        String forecast = forecastData.get(position);
        holder.forecastTextView.setText(forecast);
    }

    @Override
    public int getItemCount() {
        return forecastData != null ? forecastData.size() : 0;
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView forecastTextView;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            forecastTextView = itemView.findViewById(R.id.tvForecastItem);
        }
    }
}


