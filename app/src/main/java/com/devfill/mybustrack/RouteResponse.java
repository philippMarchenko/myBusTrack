package com.devfill.mybustrack;

import java.util.List;

public class RouteResponse {
	public List<Route> routes;
	
	
	public String getPoints() {
        return this.routes.get(0).overview_polyline.points;
    }
	

    public String getDurationRout() {
        return this.routes.get(0).legs.get(0).duration.text;
    }
    public String getDistanceRout() {
        return this.routes.get(0).legs.get(0).distance.text;
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
