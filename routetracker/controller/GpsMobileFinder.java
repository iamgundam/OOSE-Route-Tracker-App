package routetracker.controller;

//FILE: GpsMobileFinder
//AUTHOR: Joel Chia
//Implementation of Template pattern GpsLocator stub, takes information
// received from the mobile device's GPS reader when it observes that
// information has been given.

import java.util.*;

public class GpsMobileFinder extends GpsLocator
{
    private double lat;
    private double lon;
    private double alt;

    //Observer, called when new location is given by GPS reader
    protected void locationReceived(double latitude, double longitude,
                                    double altitude)
    {
        lat = latitude;
        lon = longitude;
        alt = altitude;
    }
     
    public double getLat()
    {
        return lat;
    }
    
    public double getLon()
    {
        return lon;
    }
    
    public double getAlt()
    {
        return alt;
    }
   
}//end class
