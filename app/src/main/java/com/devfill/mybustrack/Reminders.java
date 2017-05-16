package com.devfill.mybustrack;

public class Reminders {
		public static String[] routeId = new String[10];
		public static String[] distance = new String[10];
		public static String[] duration = new String[10];
		public static String[] durationReal = new String[10];
		static int count = 0;
		
		public static void initReminder(String rId,String dist,String dur,String durReal){
			count++;
			routeId[count] = rId;
			distance[count] = dist;
			duration[count] = dur;
			durationReal[count] = durReal;
			
		}
		public static void deInitReminder(){
			
			routeId[count] = " ";
			distance[count] = " ";
			duration[count] = " ";
			durationReal[count] = " ";
			count = 0;
		}
		
	}