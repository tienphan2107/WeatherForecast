package com.example.main;

public class HourlyForecast {
    private String time;
    private double temperature;

    private String summary;

    public HourlyForecast(String time, double temperature, String summary) {
        this.time = time;
        this.temperature = temperature;
        this.summary = summary;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}

