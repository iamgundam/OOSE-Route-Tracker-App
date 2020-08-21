package routetracker.model;

//FILE: Subpoint.java
//AUTHOR: Joel Chia (19170028)
// Subclass to Waypoint, holds the key to the subroute it should travel to
// and a reference to the map it was given when built.

import java.util.*;
import routetracker.controller.GeoUtils;

public class Subpoint extends Waypoint
{
    //CLASS FIELDS ------------------------------------------------------------

    private String subName;
    private Map<String,List<Point>> routes;

    //CONSTRUCTORS ------------------------------------------------------------

    //Alternate
    public Subpoint(double inLat, double inLon, double inAlt, String inDesc, 
                    String inName, Map<String,List<Point>> inRoutes)
    throws IllegalArgumentException
    {
        super(inLat, inLon, inAlt, inDesc);
        setName(inName);
        routes  = inRoutes;
    }

    //Copy
    public Subpoint(Subpoint copyFrom)
    {
        super(copyFrom);
        subName = copyFrom.getName();
        routes  = copyFrom.getRoutes();
    }

    public String getName()
    {
        return subName;
    }

    public Map<String,List<Point>> getRoutes()
    {
        return routes;
    }

    public void setName(String inName)
    throws IllegalArgumentException
    {
        if(super.validate(inName, "^[a-zA-Z0-9_ ]+$"))
        {
            subName = inName;
        }
        else
        {
            throw new IllegalArgumentException("Input \""+inName+"\" contains"+
                                               " invalid characters!");
        }
    }

    //FUNCTIONS ---------------------------------------------------------------

    //Effectively inserts subroute into current route and sends caller down
    // the subroute until it ends at the equivalent main route node of the sub
    // end node.
    @Override public Point travel()
    {
        LinkedList<Point> subRoute;
        Waypoint previous;        

        //Retrieve the subroute and make a copy of it
        subRoute = new LinkedList<Point>();
        subRoute.addAll(routes.get(subName));
        
        //Remove last node (endpoint), not needed
        subRoute.removeLast();

        //Update second last node to point to this route's next waypoint at end
        // Downcast is safe as only the last node is not a waypoint, which was
        // removed already
        // Also overwrites second last -> main next node's segment description
        previous = (Waypoint)subRoute.removeLast();
        previous.setNext(super.getNextPoint());
        previous.setDesc(super.getNextPoint().getDesc());

        //Re-add nodes into copied subroute
        subRoute.add(previous);
        subRoute.add(super.getNextPoint());

        //Send caller down the subroute. Returns second node as this node is
        // already the first
        return subRoute.getFirst().travel();
    }
        

    public boolean equals(Object other, GeoUtils distCalc)
    {
        Subpoint inSP;
        boolean isEqual;

        isEqual = false;
        if(super.equals(other, distCalc))
        {
            if(other instanceof Subpoint)
            {
                inSP = (Subpoint)other;
                if(subName.equals(inSP.getName()))
                {
                    isEqual = true;
                }
            }
        }

        return isEqual;       
    }//end equals

}//end class
