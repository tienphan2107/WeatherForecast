package com.example.main;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TextView tvCurrentWeather;
    private TextView tvCurrentLocation;
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;

    private RecyclerView rvHourlyForecast, rvDailyForecast;
    private HourlyForecastAdapter hourlyForecastAdapter;
    private DailyForecastAdapter dailyForecastAdapter;

    private List<HourlyForecast> hourlyForecastList = new ArrayList<>();
    private List<DailyForecast> dailyForecastList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentWeather = findViewById(R.id.tvCurrentWeather);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup RecyclerView
        rvHourlyForecast = findViewById(R.id.rvHourlyForecast);
        rvDailyForecast = findViewById(R.id.rvDailyForecast);

        // Gán LayoutManager cho RecyclerView
        rvHourlyForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDailyForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        hourlyForecastAdapter = new HourlyForecastAdapter(hourlyForecastList);
        dailyForecastAdapter = new DailyForecastAdapter(dailyForecastList);

        rvHourlyForecast.setAdapter(hourlyForecastAdapter);
        rvDailyForecast.setAdapter(dailyForecastAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Ứng dụng cần quyền truy cập vị trí để hiển thị thời tiết.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            getWeatherData(lat, lon);

                            // Lấy tên thành phố
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    String cityName = addresses.get(0).getLocality();
                                    if (cityName == null || cityName.isEmpty()) {
                                        cityName = addresses.get(0).getSubAdminArea();
                                    }
                                    if (cityName == null || cityName.isEmpty()) {
                                        cityName = addresses.get(0).getAdminArea();
                                    }
                                    tvCurrentLocation.setText("Vị trí: " + cityName);
                                } else {
                                    tvCurrentLocation.setText("Không tìm thấy thành phố");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                tvCurrentLocation.setText("Lỗi lấy tên thành phố");
                            }

                            // Hiển thị vị trí trên bản đồ
                            if (mMap != null) {
                                LatLng userLocation = new LatLng(lat, lon);
                                mMap.addMarker(new MarkerOptions().position(userLocation).title("Vị trí của bạn"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
                            }

                        } else {
                            tvCurrentWeather.setText("Không thể lấy vị trí.");
                        }
                    }
                });
    }

    private void getWeatherData(double lat, double lon) {
        String apiKey = "W90UlNacTQRI9BUUPweMfX9czp4BQQhl";
        String url = "https://api.pirateweather.net/forecast/" + apiKey + "/" + lat + "," + lon + "?units=si&lang=vi";

        Log.d("WeatherAPI", "URL: " + url);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> tvCurrentWeather.setText("Lỗi kết nối thời tiết."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonData);
                        JSONObject currently = json.getJSONObject("currently");
                        JSONObject daily = json.getJSONObject("daily");
                        JSONObject hourly = json.getJSONObject("hourly");
                        JSONArray dataArray = daily.getJSONArray("data");
                        JSONArray hourlyData = hourly.getJSONArray("data");
                        JSONObject today = dataArray.getJSONObject(0); // Dự báo cho ngày hôm nay

                        double temp = currently.getDouble("temperature");
                        String summary = currently.getString("summary");
                        double tempMin = today.getDouble("temperatureLow");
                        double tempMax = today.getDouble("temperatureHigh");

                        double finalTempMax = tempMax;
                        double finalTempMin = tempMin;
                        double finalTemp = temp;
                        //hien thi thoi tiet hom nay
                        runOnUiThread(() -> {
                            tvCurrentWeather.setText(
                                    "Nhiệt độ: " + finalTemp + "°C\n" +
                                            "Trạng thái: " + summary + "\n" +
                                            "Cao nhất: " + finalTempMax + "°C\n" +
                                            "Thấp nhất: " + finalTempMin + "°C"
                            );
                        });
                        // hiển thị thời tiết 7 ngày tiếp theo
                        dailyForecastList.clear();
                        for (int i = 1; i < dataArray.length(); i++) {
                            JSONObject day = dataArray.getJSONObject(i);

                            long time = day.getLong("time");
                            tempMin = day.getDouble("temperatureMin");
                            tempMax = day.getDouble("temperatureMax");
                            String summaryDay = day.getString("summary"); // 👉 lấy mô tả

                            String dateStr = convertUnixToDate(time);
                            dailyForecastList.add(new DailyForecast(dateStr, tempMin, tempMax, summaryDay));
                        }
                        runOnUiThread(() -> dailyForecastAdapter.notifyDataSetChanged());

                        // hiển thị thời tiết trong ngày (theo giờ)
                        hourlyForecastList.clear();
                        for (int i = 0; i < hourlyData.length(); i++) {
                            JSONObject hourObj = hourlyData.getJSONObject(i);
                            long time = hourObj.getLong("time");
                            temp = hourObj.getDouble("temperature");
                            String summaryHour = hourObj.getString("summary"); // 👉 lấy mô tả

                            String hourStr = convertUnixToHour(time);
                            hourlyForecastList.add(new HourlyForecast(hourStr, temp, summaryHour));
                        }
                        runOnUiThread(() -> hourlyForecastAdapter.notifyDataSetChanged());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> tvCurrentWeather.setText("Lỗi phân tích dữ liệu thời tiết."));
                    }
                }
            }
        });
    }
    private String convertUnixToDate(long unixTime) {
        Date date = new Date(unixTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM", new Locale("vi"));
        return sdf.format(date);
    }
    private String convertUnixToHour(long unixTime) {
        Date date = new Date(unixTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("vi"));
        return sdf.format(date);
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Không có quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
        }
    }
}
