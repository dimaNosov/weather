package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Place;
import model.Weather;
import newpack.packs;

/**
 * Created by dimka on 08.07.2017.
 */

public class JSONWeatherParser {

    public static Weather getWether(String data){
        Weather weather = new Weather();

        try {
            JSONObject jsonObject = new JSONObject(data);

            Place place = new Place();
            JSONObject coordObj = packs.getObject("coord", jsonObject);
            place.setLat(packs.getFloat("lat", coordObj));
            place.setLon(packs.getFloat("lon", coordObj));

            JSONObject sysObj = packs.getObject("sys", jsonObject);
            place.setCountry((packs.getString("country", sysObj)));
            place.setLastupdate(packs.getInt("dt", jsonObject));
            place.setSunrise((packs.getInt("sunrise", sysObj)));
            place.setSunset(packs.getInt("sunset", sysObj));
            place.setCity(packs.getString("name", jsonObject));
            weather.place = place;

            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(packs.getInt("id", jsonWeather));
            weather.currentCondition.setDescription(packs.getString("description", jsonWeather));
            weather.currentCondition.setCondition(packs.getString("main", jsonWeather));
            weather.currentCondition.setIcon(packs.getString("icon", jsonWeather));

            JSONObject mainObj = packs.getObject("main", jsonObject);
            weather.currentCondition.setHumidity(packs.getInt("humidity", mainObj));
            weather.currentCondition.setPressure(packs.getInt("pressure",mainObj));
            weather.currentCondition.setMinTemp(packs.getFloat("temp_min", mainObj));
            weather.currentCondition.setMaxTemp(packs.getFloat("temp_max", mainObj));
            weather.currentCondition.setTemperature(packs.getFloat("temp", mainObj));


            JSONObject windObj = packs.getObject("wind", jsonObject);
            weather.wind.setSpeed(packs.getFloat("speed", windObj));
            weather.wind.setDeg(packs.getFloat("deg", windObj));
            JSONObject cloudObj = packs.getObject("clouds", jsonObject);
            weather.clouds.setPrecipitation(packs.getInt("all", cloudObj));

            return weather;


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }
}
