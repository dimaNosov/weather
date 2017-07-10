package com.example.dimka.weather;

import android.content.DialogInterface;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import data.CityChange;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class Main extends AppCompatActivity {
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

        CityChange cityChange = new CityChange(Main.this);


        renderWeatherData(cityChange.getCity());

        ImageButton cc = (ImageButton) findViewById(R.id.btnset);
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }
    public void renderWeatherData(String city) {

        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&appid=6597670c9428b0655b4b69632d7628cc&units=metric"});
    }







    private class WeatherTask extends AsyncTask<String, Void, Weather> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Weather weather) {

            super.onPostExecute(weather);

            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(new Date(weather.place.getSunrise()));
            String sunsetDate = df.format(new Date(weather.place.getSunset()));
            String lastUpDate = df.format(new Date(weather.place.getLastupdate()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());


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
                cityChange.setCity(cityInput.getText().toString());

                String newCity = cityChange.getCity();

                renderWeatherData(newCity);

            }

        });
        builder.show();
    }
}
