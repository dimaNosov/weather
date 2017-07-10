package data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by dimka on 09.07.2017.
 */

public class CityChange {
    SharedPreferences prefs;
    public CityChange(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity() {
        return prefs.getString("city", "Saratov,RU");
    }

    public void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
