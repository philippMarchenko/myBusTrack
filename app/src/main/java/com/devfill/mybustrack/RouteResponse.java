package com.devfill.mybustrack;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RouteResponse  {
	public List<Route> routes = new ArrayList<Route>();


	public String getPoints() {
      //  if(routes.size() > 0)
            return this.routes.get(0).overview_polyline.points;
     //   else
         //   return "not data";
    }



	

    public String getDurationRout() {
      //  if(routes.size() > 0)
            return this.routes.get(0).legs.get(0).duration.text;
       // else
         //   return "not data";

    }
    public String getDistanceRout() {
       // if(routes.size() > 0)
            return this.routes.get(0).legs.get(0).distance.text;
      //  else
       //     return "not data";

    }
    class Route {
    	public List<Legs> legs;
        OverviewPolyline overview_polyline;
       
    }

    class OverviewPolyline {
        String points;
    }

    class Legs {
    	Duration duration;
    	Distance distance;
    }
    class Duration {
    	String text;
    }
    class Distance {
        String text;
    }
    
}
