package com.example.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder> {

    private List<HourlyForecast> hourlyForecastList;

    public HourlyForecastAdapter(List<HourlyForecast> hourlyForecastList) {
        this.hourlyForecastList = hourlyForecastList;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly_forecast, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        HourlyForecast forecast = hourlyForecastList.get(position);
        holder.timeTextView.setText(forecast.getTime());
        holder.temperatureTextView.setText(forecast.getTemperature() + "°C");
        holder.summaryTextView.setText(forecast.getSummary()); // 👉 Thêm mô tả thời tiết
    }

    @Override
    public int getItemCount() {
        return hourlyForecastList.size();
    }

    public static class HourlyViewHolder extends RecyclerView.ViewHolder {

        TextView timeTextView;
        TextView temperatureTextView;
        TextView summaryTextView; // 👉 Thêm View mô tả

        public HourlyViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.tvTime);
            summaryTextView = itemView.findViewById(R.id.tvSummary);
            temperatureTextView = itemView.findViewById(R.id.tvTemperature);
        }
    }
}


