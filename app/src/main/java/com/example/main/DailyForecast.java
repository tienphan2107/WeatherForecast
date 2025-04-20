package com.example.main;

public class DailyForecast {
    private String date;
    private double tempMin;
    private double tempMax;

    private String summary;

    public DailyForecast(String date, double tempMin, double tempMax, String summary) {
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.summary =  summary;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

