package routetracker.controller;

//Stub class for receiving current GPS location
import java.util.*;

public abstract class GpsLocator
{

    //hook
    protected abstract void locationReceived(double latitude, double longitude,
                                             double altitude);
}

