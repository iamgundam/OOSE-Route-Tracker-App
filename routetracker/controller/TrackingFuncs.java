package routetracker.controller;

//FILE: TrackingFuncs.java
//AUTHOR: Joel Chia (19170028)

import java.util.*;
import java.io.*;
import java.lang.Thread.*;

import routetracker.model.Point;
import routetracker.view.UserInterface;

public class TrackingFuncs
{
    
    //Builds a string representation of each route's name, start/end
    // co-ordinates and each route's individual overall distance in the form of
    // horizontal, vertical up and vertical down metres.
    public ArrayList<String> buildRouteTable(Map<String, List<Point>> routes, 
                                        GeoUtils geo)
    {
        String name;
        List<Point> route;
        ArrayList<String> table;
        Set<Map.Entry<String, List<Point>>> entries;
        Point currNode, prev;
        double bLat, bLon, bAlt, eLat, eLon, eAlt;
        double totalHorz, totalUp, totalDown, diff;

        table = new ArrayList<String>();
        entries = routes.entrySet();
        prev = null;
    
        table.add("%---------------------------------------------------%");
        //For each route in the map, extract information and add to table
        for(Map.Entry<String, List<Point>> entry : entries)
        {
            totalHorz = 0.0;
            totalUp   = 0.0;
            totalDown = 0.0;

            name = entry.getKey();
            route = entry.getValue();

            //Starting co-ordinates in first node
            currNode = route.get(0);
            bLat = currNode.getLat();
            bLon = currNode.getLon();
            bAlt = currNode.getAlt();

            //Travel to end node, which returns null.
            //Also record distance to travel.
            prev = currNode;
            currNode = prev.travel();
            while(currNode != null)
            {

                //Use GeoUtils to calculate horizontal distance
                totalHorz +=
                           geo.calcMetresDistance(prev.getLat(), prev.getLon(),
                                         currNode.getLat(), currNode.getLon());

                //Ascent will be negative e.g(5-10 = -5 or -10-(-5) = -5)
                //Descent will be positive e.g(10-5 = 5 or -5-(-10) = 5)
                diff = prev.getAlt() - currNode.getAlt();
                if(diff > 0)
                {
                    totalDown = totalDown + diff;
                }
                else
                {
                    totalUp = totalUp + -1.0*(diff);
                }
                prev = currNode;
                currNode = prev.travel();
            }
            currNode = prev; //return to last node after being set to null

            eLat = currNode.getLat();
            eLon = currNode.getLon();
            eAlt = currNode.getAlt();

            table.add("Route name: "+name+"\n"+
                      "Begins at:  "+bLat+","+bLon+","+bAlt+"\n"+
                      "Ends at:    "+eLat+","+eLon+","+eAlt+"\n"+
                      "Total distance \n"+
                      "Horizontal: "+totalHorz+"m\n"+
                      "Ascent:     "+totalUp+"m\n"+
                      "Descent:    "+totalDown+"m\n"+
                      "%---------------------------------------------------%"
                     );
        }//end for each

        return table;
    }//end buildRouteTable

    //Show a route's description, lists all of its waypoints and segment
    // descriptions.
    public String showDetails(Map<String, List<Point>> routes, String name)
    throws IllegalArgumentException
    {
        List<Point> theRoute;
        Point currNode, prev;
        String desc, output;
        int ii;

        theRoute = routes.get(name);
        if(theRoute == null)
        {
            throw new IllegalArgumentException("\""+name+"\" does not exist "+
                                               "in input route map!");
        }

        ii = 1;
        output = "";
        currNode = theRoute.get(0);
        prev = currNode;
        currNode = currNode.travel();
        while(currNode != null)
        {
            desc = prev.getDesc();
            if(desc.equals(""))
            {
                desc = "No description.";
            }

            output = output +
                     "Waypoint "+ii+" to "+(ii+1)+"\n"+
                     desc+"\n"+
                     prev.getLat()+","+prev.getLon()+","+prev.getAlt()+" to\n"+
                     currNode.getLat()+","+currNode.getLon()+","+
                     currNode.getAlt()+"\n";

            prev = currNode;
            currNode = currNode.travel();
            ii++;
        }//end while
    
        //Insert route description into beginning
        output = prev.getDesc() + "\n" + output;
        return output;
    }//end showDetails

    public void routeTracker(Map<String, List<Point>> routes, String name,
                             GpsMobileFinder gps, GeoUtils geo)
    throws IllegalArgumentException
    {
        final double TOL = 0.001;
        double currLat, currLon, currAlt, nodeLat, nodeLon, nodeAlt;
        double dist, altDist;
        List<Point> theRoute;
        Point currNode;



        //Check if route exists
        theRoute = routes.get(name);
        if(theRoute == null)
        {
            throw new IllegalArgumentException("No such route \""+name+"\"!");
        }

        currNode = theRoute.get(0);
        while(currNode != null)
        {
            //Retrieve current waypoint's position
            nodeLat = currNode.getLat();
            nodeLon = currNode.getLon();
            nodeAlt = currNode.getAlt();

            //Retrieve current co-ordinates
            currLat = gps.getLat();
            currLon = gps.getLon();
            currAlt = gps.getAlt();

            UserInterface.display("Current location:\n"+
                                  currLat+", "+currLon+", "+currAlt+"\n");

            //Find horizontal and vertical distance difference
            altDist = nodeAlt - currAlt;
            dist = geo.calcMetresDistance(nodeLat, nodeLon, 
                                              currLat, currLon);

            //If position effectively equal to waypoint, advance
            if((dist < 10.0 || currNode.realEquals(dist, 10.0, TOL)) &&
               (Math.abs(altDist) < 2.0 || 
                currNode.realEquals(Math.abs(altDist), 2.0, TOL)))
            {
                UserInterface.display("Waypoint reached!");
                currNode = currNode.travel();
                
            }
            else
            //Report progress
            {
                UserInterface.display("Remaining distance to next node\n"+
                                      "Horizontal: "+dist+"\n"+
                                      "Vertical:   "+altDist);
            }

            //Wait 3 seconds before next update
            try
            {
                Thread.sleep(3000);
            }
            catch(InterruptedException e){ }
        }
        UserInterface.display("The route has been completed."+
                              "\n Returning to menu...");
    
    }//end routeTracker

}//end class
