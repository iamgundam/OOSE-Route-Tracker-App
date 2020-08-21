package routetracker.controller;

import java.util.*;
import java.io.*;

public class GeoUtils
{
    public double calcMetresDistance(double lat1, double long1,
                                     double lat2, double long2)
    {
        /* Gives a strange, overly large value.
        double a, b, c, result;

        a = Math.sin((Math.PI*lat1)/180.0)*Math.sin((Math.PI*lat2)/180.0);
        b = Math.cos((Math.PI*lat1)/180.0)*Math.sin((Math.PI*lat2)/180.0);
        c = Math.cos((Math.PI*Math.abs(long1-long2))/180.0);
        
        result = 6371000.0*(1.0/(Math.cos(a+b*c)));
        */

        return (lat1+lat2-long1+long2);
    }//end calcMetresDistance

    public String retrieveRouteData()
    throws IOException
    {
        String output;
        output = ("helloRoute theTest \n"+
                  "10.00, 30.00, 1.33,does this \n"+
                  "50.00, 20.00, 4.33,work?");
        return output;
    }

}//end class
