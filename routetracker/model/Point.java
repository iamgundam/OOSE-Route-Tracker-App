package routetracker.model;

//FILE: Point.java
//AUTHOR: Joel Chia (19170028)
// Abstract root class for composite pattern structure. Has Waypoints or
// Subpoints as nodes and Endpoints as leaves.

import java.util.*;
import java.util.regex.*;

import routetracker.controller.GeoUtils;

public abstract class Point
{
    //Constant tolerance level when comparing reals
    private final double TOL = 0.001;

    //latitude, longitude, altitude
    private double lat; //-90.0 to 90.0, south to north pole
    private double lon; //-180.0 to 180.0
    private double alt; //-50000 to 50000, to prevent overflow

    private String desc;    

    //CONSTRUCTORS ------------------------------------------------------------

    //Alternate
    public Point(double inLat, double inLon, double inAlt, String inDesc)
    throws IllegalArgumentException
    {
        //If not within valid range, abort
        if(!realRange(inLat, -90.0, 90.0, TOL))
        {
            throw new IllegalArgumentException("Invalid latitude: "+inLat);
        }
        if(!realRange(inLon, -180.0, 180.0, TOL))
        {
            throw new IllegalArgumentException("Invalid longitude: "+inLon);
        }
        if(!realRange(inAlt, -50000.0, 50000.0, TOL))
        {
            throw new IllegalArgumentException("Invalid altitude: "+inAlt);
        }

        lat = inLat;
        lon = inLon;
        alt = inAlt;
        setDesc(inDesc);
    }

    //Copy
    public Point(Point inPoint)
    {
        lat = inPoint.getLat();
        lon = inPoint.getLon();
        alt = inPoint.getAlt();
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
    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String inDesc)
    throws IllegalArgumentException
    {
        validateDesc(inDesc);
        desc = inDesc;
    }

    //Different point types enforce different formats
    abstract protected void validateDesc(String inDesc);

    //FUNCTIONS ---------------------------------------------------------------
    //Subclasses implement the travel function
    abstract public Point travel();

    public boolean equals(Object other, GeoUtils distCalc)
    {
        Point inP;
        double dist;
        boolean isEqual;

        isEqual = false;
        if(other instanceof Point)
        {
            inP = (Point)other;
            dist = distCalc.calcMetresDistance(inP.getLat(), inP.getLon(),
                                               lat, lon                    );

            if(realEquals(alt, inP.getAlt(), 2.0))
            {
                if(dist < 10.0 || realEquals(dist, 10.0, TOL))
                {
                    isEqual = true;
                }
            }
        }

        return isEqual;
    }//end equals

    //Real number comparison for equality with a tolerance parameter to adjust
    // for desired accuracy.
    public boolean realEquals(double a, double b, double tolerance)
    {
        boolean isEqual;
        double diff;

        isEqual = false;
        diff = Math.abs(a-b);
        if(diff <= tolerance)
        {
            isEqual = true;
        }

        return isEqual;
    }//end realEquals

    //Determines whether 'num' is within the range of 'a' to 'b' inclusive.
    public boolean realRange(double num, double a, double b, 
                                double tolerance)
    {
        boolean isEqual;
        isEqual = false;

        //If range input reversed, run realRange with correctly ordered input
        if(a > b)
        {
            isEqual = realRange(num, b, a, tolerance);
        }
        //Otherwise, run normally and compare
        else if(a < b)
        {
            if((num < b && num > a) || 
               realEquals(num, b, tolerance) || realEquals(num, a, tolerance))
            {
                isEqual = true;
            }
        }
        //If neither a < b or b < a, a must equal b. Check for equality to num.
        else
        {
            isEqual = realEquals(num, a, tolerance);
        }
           
        return isEqual;
    }//end realRange

    //Identifies if an input string matches the pattern given.
    protected boolean validate(String in, String pattern)
    {
        boolean isValid = false;

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(in);
        isValid   = m.matches();

        return isValid;
    }//end validate
}//end class
