package routetracker.model;

//FILE: FileIO.java
//AUTHOR: Joel Chia (19170028)
// Factory class that reads in and creates Route objects stored in file on
// initial load of the route tracker app and handles the file IO for updating
// using an input file retrieved from the server.

import java.util.*;
import java.util.regex.*;
import java.util.LinkedList.*;
import java.io.*;

import routetracker.exceptions.RouteLoadException;

public class FileIO
{
    //Factory method, scans from either a file or the string input directly
    public Map<String,List<Point>> buildRoutes(String input)
    throws RouteLoadException
    {
        Scanner sc;
        Map<String,List<Point>> routes;

        try
        {
            //Check for .txt file, can indicate parent directories
            if(validate(input, "(\\.\\.\\/)*\\w+\\.(txt)"))
            {
                sc = new Scanner(new File(input));
                routes = initRoutes(input, sc);
            }
            else
            {
                sc = new Scanner(input);
                routes = initRoutes(input, sc);
            }
        }
        catch(FileNotFoundException e)
        {
            throw new RouteLoadException("File \""+input+"\" not found", e);
        }

        return routes;
    }//end buildRoutes

    private Map<String,List<Point>> initRoutes(String fileName, Scanner sc)
    throws RouteLoadException
    {
        String line, pointDesc, name, desc;
        String[] lineArray, subLine;
        LinkedList<Point> currRoute;
        ArrayList<String> subList;
        Map<String,List<Point>> routes;
        Waypoint previous;
        Point curr;
        boolean isSub, firstNode, keepGoing;
        double lat, lon, alt;

        name = "";
        desc = "";
        pointDesc = "";
        subLine = null;
        isSub = false;
        keepGoing = true;
        firstNode = true;
        currRoute = null;

        lat = 100.0;
        lon = 100.0;
        alt = 100000.0;
        routes = new HashMap<String,List<Point>>();
        subList = new ArrayList<String>(100);

        try
        {//--------------------------------------------------------------------
            
        while(sc.hasNextLine())
        {
            //Find next non-empty line
            line = sc.nextLine();
            while(line.equals("") && sc.hasNextLine())
            {
                line = sc.nextLine();
            }

            lineArray = line.split(" ", 2);
            currRoute = new LinkedList<Point>();
            firstNode = true;
            keepGoing = true;
            
            name = lineArray[0];
            desc = lineArray[1];

            //Proceed if name is valid
            if(validate(name, "^[a-zA-z0-9_]+$") && sc.hasNextLine())
            {
                //While next line is not empty
                line = sc.nextLine();
                while(line.length() != 0 && keepGoing)
                {
                    lineArray = line.split(",", 4);
                    lat = Double.parseDouble(lineArray[0]);
                    lon = Double.parseDouble(lineArray[1]);
                    alt = Double.parseDouble(lineArray[2]);

                    //If description not empty, element 3 will exist
                    if(lineArray.length == 4)
                    {
                        pointDesc = lineArray[3];

                        if(validate(pointDesc, "^\\*.*"))
                        {
                            isSub = true;
                        }
                        //else, a segment description.
                    }

                    //Get next line before while loop checks for non-empty
                    if(sc.hasNextLine())
                    {
                        line = sc.nextLine();
                    }
                    else
                    //No lines left, so stop (EOF)
                    {
                        keepGoing = false;
                    }

                    //Create node objects, throws IllegalArgumentException 
                    // if params invalid
                    // Make Waypoint/Subpoint until end, which will be Endpoint
                    // End is found by either EOF or next line is empty
                    if((line.length() == 0) || !keepGoing)
                    {
                        curr = new Endpoint(lat, lon, alt, desc);
                    }
                    else
                    {
                        if(isSub)
                        {
                            //Remove asterisk at beginning and remember
                            // the subroute call to check later
                            pointDesc = pointDesc.substring(1);
                            subList.add(pointDesc);
                            isSub = false;
                            curr = new Subpoint(lat, lon, alt, desc, 
                                                  pointDesc, routes);
                        }
                        else
                        {
                            curr = new Waypoint(lat, lon, alt, pointDesc);
                        }
                    }

                    //Link previous node to current node
                    if(!firstNode)
                    {
                        //Downcast is safe as there are only Waypoint
                        // and Subpoint (subclass of Waypoint) objects 
                        // in the list, no linking is done at end.
                        previous = (Waypoint)currRoute.removeLast();
                        previous.setNext(curr);
                        currRoute.add(previous);
                    }

                    firstNode = false;
                    currRoute.add(curr);
                }//end while (point information)    

                routes.put(name, currRoute);
            }//end if (validate route name/description)
        }//end while (read in loop)

        //Check for every subroute that it exists as a main route
        subList.trimToSize();
        for(int ii=0;ii < subList.size();ii++)
        {
            pointDesc = subList.get(ii);
            if(routes.get(pointDesc) == null)
            {
                throw new IllegalArgumentException("Subroute \""+pointDesc+
                                                   "\" not found!");
            }
        }

        }//--------------------------------------------------------------------
        catch(IllegalArgumentException e)
        {
            throw new RouteLoadException(("Input error! "+ e.getMessage()), e);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new RouteLoadException(("Bad input format! " +
                                          e.getMessage()), e);
        }

        return routes;
    }//end initRoutes
 
   //Identifies if an input string matches the pattern given.
    private boolean validate(String input, String pattern)
    {
        boolean isValid = false;

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        isValid   = m.matches();
        
        return isValid;
    }//end validateName

}//end class
