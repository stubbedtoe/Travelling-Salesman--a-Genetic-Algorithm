/**
	Class to model a town/vertex. These are what Paths are made of.

	Andrew Healy - HDipIT - 13250280 - April 2014
*/

public class Town{
	
	public int id;
	public String name;
	public double latitude;
	public double longitude;

	//constructor
	public Town(int id, String name, double latitude, double longitude){
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	//return the distance between two latitude/longitude points im kilometres - uses Haversine
	public double distanceTo(Town to){

		if(this == to){
			return 0;//same town
		}

		//radius of the earth = 6371km
		double fromLat = Math.toRadians(latitude);
		double fromLong = Math.toRadians(longitude);
		double toLat = Math.toRadians(to.latitude);
		double toLong = Math.toRadians(to.longitude);

		//implementation of haversin formula
		return (2 * 6371 * Math.asin(Math.sqrt(Math.pow(Math.sin((fromLat-toLat)/2.0),2)+(Math.cos(fromLat)*Math.cos(toLat)*Math.pow(Math.sin((toLong-fromLong)/2.0),2)))));
	}

	public Town copy(){
		Town toReturn = new Town(id, name, latitude, longitude);
		return toReturn;
	}

}