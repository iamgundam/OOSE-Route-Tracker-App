package routetracker.exceptions;

//Exception class: For FileIO class
//  Indicates that an error has occurred when attempting to load an input route
//  file.

public class RouteLoadException extends Exception
{
    public RouteLoadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
