package com.parking.management.beans;

import java.util.ArrayList;
import java.util.List;

public class ParkingLocations {
	private List<ParkingLocation> locations = new ArrayList<>();
	
	public ParkingLocations() {}
	
	public ParkingLocations(List<ParkingLocation> locations) {
		super();
		this.locations = locations;
	}

	public List<ParkingLocation> getLocations() {
		return locations;
	}

	public void setLocations(List<ParkingLocation> locations) {
		this.locations = locations;
	}

}
