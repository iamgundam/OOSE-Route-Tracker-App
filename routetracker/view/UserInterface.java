package routetracker.view;

//FILE: UserInterface.java
//AUTHOR: Joel Chia (19170028)
// Interprets user input and outputs to console.

import java.util.*;
import java.util.regex.*;
import java.io.*;

import routetracker.model.Point;
import routetracker.model.FileIO;
import routetracker.controller.*;
import routetracker.exceptions.RouteLoadException;

public class UserInterface
{
    public static void main(String[] args)
    {
        String output, option, routeChoice, detailChoice;
        boolean keepRunning;
        Map<String, List<Point>> routes;

        FileIO fio          = new FileIO();
        TrackingFuncs funcs = new TrackingFuncs();
        GeoUtils geo        = new GeoUtils();
        GpsMobileFinder gps = new GpsMobileFinder();
        ArrayList<String> table = new ArrayList<String>();

        routes = null;
        routeChoice = "";

        //Load routes
        try
        {
            routes = fio.buildRoutes("testInput.txt");
            keepRunning = true;
        }
        catch(RouteLoadException e)
        {
            System.out.println("Error loading routes from file, \n"+
                               "downloading from server instead");
            try
            {
                System.out.println(e.getMessage());
                routes = fio.buildRoutes(geo.retrieveRouteData());
                keepRunning = true;
            }
            catch(RouteLoadException e2)
            {
                System.out.println("Error loading routes from server, \n"+
                                   "shutting down...");
                keepRunning = false;
            }
            catch(IOException e2)
            {
                System.out.println("Connection to server not found or \n"+
                                   "interrupted, shutting down...");
                keepRunning = false;
            }
            
        }

        //Run program
        while(keepRunning)
        {
            table = funcs.buildRouteTable(routes, geo);
            for(int ii=0;ii<table.size();ii++)
            {
                System.out.println(table.get(ii));
            }

            option = inputString("Input choice:");
            
            Pattern pDetail  = Pattern.compile("^(detail) .*");
            Matcher inDetail = pDetail.matcher(option);

            Pattern pSelect  = Pattern.compile("^(select) .*");
            Matcher inSelect = pSelect.matcher(option);

            //Help
            if(option.equals("*!help"))
            {   
                System.out.println(
              "detail <routeName>  Displays the given route in more detail.\n"+
              "select <routeName>  Selects a route to follow when tracking.\n"+
              "update              Retrieves routes from the server.\n"+
              "go                  Begins tracking with the selected route.\n"+
              "off                 Ends RouteTracker.");
            }
            //Turn off
            else if(option.equals("off"))
            {
                keepRunning = false;
            }
            //Update routes from server
            else if(option.equals("update"))
            {
                try
                {
                    routes = fio.buildRoutes(geo.retrieveRouteData());
                }
                catch(RouteLoadException e)
                {
                    System.out.println("Error loading from server");
                }
                catch(IOException e)
                {
                    System.out.println("Connection lost/interrupted");
                }
            }
            //Show details of a route
            else if(inDetail.matches())
            {
                detailChoice = option.split(" ", 2)[1];
                try
                {
                    System.out.println(funcs.showDetails(routes,detailChoice));
                }
                catch(IllegalArgumentException e)
                {
                    System.out.println("No such route!");
                }
            }
            //Select a route to track
            else if(inSelect.matches())
            {
                routeChoice = option.split(" ", 2)[1];
                if(!routes.containsKey(routeChoice))
                {
                    System.out.println("No such route!");
                    routeChoice = "";
                }
            }
            //Begin tracking of the selected route
            else if(option.equals("go"))
            {
                if(!routeChoice.equals(""))
                {
                    funcs.routeTracker(routes, routeChoice, gps, geo);
                }
                else
                {
                    System.out.println("No route selected!");
                }
            }
            else
            {
                System.out.println("Invalid input.");
            }//end if (choice validation)

        }//end while

    }//end main

    public static String inputString(String prompt)
    {
        String input;
        boolean valid;

        System.out.println(prompt);
        Scanner sc = new Scanner(System.in);
        input = null;
        valid = false;

        do
        {
            try
            {
                input = sc.nextLine();
                if(input.equals(""))
                {
                    throw new InputMismatchException();
                }
                valid = true;
            }
            catch(InputMismatchException e)
            {
                sc = new Scanner(System.in);
                System.out.println("Invalid input!\n"+
                                   "Type \"*!help\" for list of commands.");
            }
        }
        while(!valid);

        return input;
    }//end inputString

    //For separation of concerns, can be replaced with other methods of display
    public static void display(String input)
    {
        System.out.println(input);
    }//end display

}//end class
