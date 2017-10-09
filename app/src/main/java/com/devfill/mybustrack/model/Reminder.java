package com.devfill.mybustrack.model;

public class Reminder {

        private String route;
		private String distance;
		private String duration;
		private String durationReal;


    public  Reminder(String route,String distance,String duration,String durationReal){
        this.route = route;
        this.distance = distance;
        this.duration = duration;
        this.durationReal = durationReal;
    }
    public  Reminder(){
    }


    public void setRoute(String route) {
        this.route = route;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDurationReal(String durationReal) {
        this.durationReal = durationReal;
    }

    public String getRoute() {

        return route;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getDurationReal() {
        return durationReal;
    }
		
	}