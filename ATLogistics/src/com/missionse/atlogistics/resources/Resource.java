package com.missionse.atlogistics.resources;

public class Resource {
	
	
	private ResourceType type;
	private String resourceName;
	private double lat;
	private double lon;
	private int count;
	private int fillCount;
	
	
	public Resource(String name, ResourceType t){
		resourceName = name;
		type = t;
		
		lat = 0;
		lon = 0;
		count = 0;
		fillCount = 0;
	}


	public ResourceType getType() {
		return type;
	}


	public void setType(ResourceType type) {
		this.type = type;
	}


	public String getResourceName() {
		return resourceName;
	}


	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public double getLon() {
		return lon;
	}


	public void setLon(double lon) {
		this.lon = lon;
	}


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}
	
	public int getFillCount(){
		return this.fillCount;
	}
	
	public void setFillCount(int c){
		this.fillCount = c;
	}

}
