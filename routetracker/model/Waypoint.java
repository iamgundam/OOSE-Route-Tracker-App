package routetracker.model;

//FILE: Waypoint.java
//AUTHOR: Joel Chia (19170028)

import java.util.*;
import routetracker.controller.GeoUtils;

public class Waypoint extends Point
{
    //CLASS FIELDS ------------------------------------------------------------

    private Point nextPoint;

    //CONSTRUCTORS ------------------------------------------------------------

    //Alternate
    public Waypoint(double inLat, double inLon, double inAlt, String inDesc)
    throws IllegalArgumentException
    {
        super(inLat, inLon, inAlt, inDesc);
        nextPoint = null;
    }

    //Copy
    public Waypoint(Waypoint copyFrom)
    {
        super(copyFrom);
        nextPoint = copyFrom.getNextPoint();
    }

    public Point getNextPoint()
    {
        return nextPoint;
    }

    public void setNext(Point inRoute)
    {
        nextPoint = inRoute;
    }

    @Override public void validateDesc(String inDesc)
    throws IllegalArgumentException
    {
        //Valid if description string does NOT have newline character
        if(super.validate(inDesc, ".*[\\n]+"))
        {
            throw new IllegalArgumentException("Input \""+inDesc+"\" contains"+
                                               " \"\\n\"");
        }
    }


    //FUNCTIONS ---------------------------------------------------------------
    
    //Simple travel to next node to retrieve coordinates.
    @Override public Point travel()
    {
        return nextPoint;
    }

    public boolean equals(Object other, GeoUtils distCalc)
    {
        Waypoint inWP;
        boolean isEqual;

        isEqual = false;
        if(other instanceof Waypoint)
        {
            inWP = (Waypoint)other;
            if(super.equals(inWP, distCalc))
            {
                isEqual = true;
            }
        }

        return isEqual;       
    }//end equals

}//end class
