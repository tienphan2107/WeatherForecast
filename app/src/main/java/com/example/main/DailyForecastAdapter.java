package com.example.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder> {

    private List<DailyForecast> dailyForecastList;

    public DailyForecastAdapter(List<DailyForecast> dailyForecastList) {
        this.dailyForecastList = dailyForecastList;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_forecast, parent, false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        DailyForecast forecast = dailyForecastList.get(position);
        holder.dateTextView.setText(forecast.getDate());
        holder.tempMinTextView.setText(forecast.getTempMin() + "°C");
        holder.tempMaxTextView.setText(forecast.getTempMax() + "°C");
        holder.summaryTextView.setText(forecast.getSummary()); // 👉 Gán thêm summary
    }

    @Override
    public int getItemCount() {
        return dailyForecastList.size();
    }

    public static class DailyViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView tempMinTextView;
        TextView tempMaxTextView;
        TextView summaryTextView;

        public DailyViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.tvDate);
            summaryTextView = itemView.findViewById(R.id.tvSummary);

            tempMinTextView = itemView.findViewById(R.id.tvTempMin);
            tempMaxTextView = itemView.findViewById(R.id.tvTempMax);
        }
    }
}

