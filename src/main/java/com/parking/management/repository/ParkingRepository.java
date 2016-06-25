package com.parking.management.repository;

import com.parking.management.beans.ParkingLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by VST on 24-06-2016.
 */
public interface ParkingRepository extends MongoRepository<ParkingLocation, String> {

    ParkingLocation findDistinctParkingLocationByName(String name);

    int countDistinctParkingLocationByName(String name);
}
