package model;

/**
 * Created by dimka on 08.07.2017.
 */

public class Weather {

    public Place place;
    public String icon;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Clouds clouds = new Clouds();
}
