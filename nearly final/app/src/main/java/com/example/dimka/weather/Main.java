package com.example.dimka.weather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

import data.CityChange;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;
import newpack.packs;

public class Main extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private String CityCoordinate;
    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humididty;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private ImageView th;
    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.citytxt);
        iconView = (ImageView) findViewById(R.id.thumbnailIcon);
        temp = (TextView) findViewById(R.id.thtxt);
        description = (TextView) findViewById(R.id.cloudtxt);
        humididty = (TextView) findViewById(R.id.humidtxt);
        pressure = (TextView) findViewById(R.id.presstxt);
        wind = (TextView) findViewById(R.id.windtxt);
        sunrise = (TextView) findViewById(R.id.srtxt);
        sunset = (TextView) findViewById(R.id.sstxt);
        updated = (TextView) findViewById(R.id.uptxt);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},120);
        } else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            this.onLocationChanged(null);
        }

        final CityChange cityChange = new CityChange(Main.this);


        renderWeatherData(cityChange.getCity());

        ImageButton cc = (ImageButton) findViewById(R.id.btnset);
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        Button gb = (Button) findViewById(R.id.gpsbtn);
        gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CityCoordinate != "not lucky") {
                    CityChange cityChange = new CityChange(Main.this);
                    cityChange.setCity(CityCoordinate);

                    String newCity = cityChange.getCity();

                    renderWeatherData(newCity);
                }
            }
        });
    }
    public void renderWeatherData(String city) {

        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&appid=6597670c9428b0655b4b69632d7628cc&units=metric"});
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location == null) {
            CityCoordinate = ("not lucky");

        } else {
            CityCoordinate = ("lat=" + location.getLatitude() + "&lon=" + location.getLongitude()).toString();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
    }


    private class WeatherTask extends AsyncTask<String, Void, Weather> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Weather weather) {

            super.onPostExecute(weather);

            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(new Date(weather.place.getSunrise() * 1000));
            String sunsetDate = df.format(new Date(weather.place.getSunset() * 1000));
            String lastUpDate = df.format(new Date(weather.place.getLastupdate()* 1000));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            th = (ImageView) findViewById(R.id.thumbnailIcon);


            Picasso.with(Main.this).load(packs.ICON_URL + weather.currentCondition.getIcon() + ".png").into(th);

            cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText("" + tempFormat + "°C");
            humididty.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressury: " + weather.currentCondition.getPressure() + "hPa");
            wind.setText("Wind: " + weather.wind.getSpeed() + "mps");
            sunrise.setText("Sunrise: " + sunriseDate);
            sunset.setText("Sunset: " + sunsetDate);
            updated.setText("Last updated: " + lastUpDate);
            description.setText("Condition: " + weather.currentCondition.getCondition() + "(" +
            weather.currentCondition.getDescription() + ")");
        }


        @Override
        protected Weather doInBackground(String... params) {

            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));
            weather = JSONWeatherParser.getWether(data);
            Log.v("Data: ", weather.currentCondition.getDescription());

            return weather;
        }
    }




    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setTitle("ChangeCity");
        final EditText cityInput = new EditText(Main.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Moscow,RU");
        builder.setView(cityInput);
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityChange cityChange = new CityChange(Main.this);
                cityChange.setCity("q=" + cityInput.getText().toString());

                String newCity = cityChange.getCity();

                renderWeatherData(newCity);

            }

        });
        builder.show();
    }
}
