package routetracker.model;

//FILE: Endpoint.java
//AUTHOR: Joel Chia (19170028)

import java.util.*;
import routetracker.controller.GeoUtils;

public class Endpoint extends Point
{
    //CLASS FIELDS ------------------------------------------------------------


    //CONSTRUCTORS ------------------------------------------------------------

    //Alternate
    public Endpoint(double inLat, double inLon, double inAlt, String inDesc)
    throws IllegalArgumentException
    {
        super(inLat, inLon, inAlt, inDesc);
    }

    //Copy
    public Endpoint(Endpoint copyFrom)
    {
        super(copyFrom);
        super.setDesc(copyFrom.getDesc());
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

    @Override public Point travel()
    {
        //End of the line, null indicates no more nodes.
        return null;
    }
        

    public boolean equals(Object other, GeoUtils distCalc)
    {
        Endpoint inSP;
        boolean isEqual;

        isEqual = false;
        if(super.equals(other, distCalc))
        {
            if(other instanceof Endpoint)
            {
                isEqual = true;
            }
        }

        return isEqual;       
    }//end equals

}//end class
