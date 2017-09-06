package com.parking.management.controllers;

import com.parking.management.beans.ParkingLocation;
import com.parking.management.beans.ParkingLocations;
import com.parking.management.beans.ParkingSlot;
import com.parking.management.services.ParkingManagerService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/parkingmgmt")
public class ParkingController {

    @Autowired
    ParkingManagerService service;

    @ApiOperation(value = "Provides all the parking locations available in the system.")
    @RequestMapping(value = "/locations", method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ParkingLocations.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public ParkingLocations getAllLocation() {
        ParkingLocations locations = new ParkingLocations();

        ParkingLocation localLocation;

        for (ParkingLocation location : service.findAllLocation()) {
            Integer total = location.getSlots().size() == 0 ? null : location.getSlots().size();
            localLocation = new ParkingLocation(location.getName(), location.getLattitude(),
                    location.getLongitude(), total, service.getAvailabilityForSlots(location.getSlots()),
                    location.isActive(), location.getLayout());
            locations.getLocations().add(localLocation);
        }

        return locations;
    }

    @ApiOperation(value = "Provides all the information of the parking location requested.")
    @RequestMapping(value = "/locations/{locationName}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationName", value = "Name of the location", required = true,
                    dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ParkingLocation.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public ParkingLocation getLocation(@PathVariable String locationName) {
        ParkingLocation location = service.findByLocationName(locationName);
        Integer total = location.getSlots().size() == 0 ? null : location.getSlots().size();
        ParkingLocation parkingLocation = new ParkingLocation(location.getName(), location.getLattitude(),
                location.getLongitude(), total,
                service.getAvailabilityForSlots(location.getSlots()), location.isActive(),
                location.getLayout());

        List<ParkingSlot> slots = location.getSlots();
        Collections.sort(slots);
        for (ParkingSlot slot : slots) {
            parkingLocation.getSlots().add(new ParkingSlot(slot.getName(), slot.getStatus(),
                    slot.getOwnerId()));
        }

        return parkingLocation;
    }

    @ApiOperation(value = "Provides the status of the parking location requested. " +
            "The status would contain information regarding the total slots available.")
    @RequestMapping(value = "/locations/{locationName}/_status", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationName", value = "Name of the location", required = true,
                    dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ParkingLocation.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public ParkingLocation getLocationStatus(@PathVariable String locationName) {
        ParkingLocation location = service.findByLocationName(locationName);
        Integer total = location.getSlots().size() == 0 ? null : location.getSlots().size();
        return new ParkingLocation(location.getName(), location.getLattitude(), location.getLongitude(),
                total, service.getAvailabilityForSlots(location.getSlots()), location.isActive(),
                location.getLayout());
    }

    @ApiOperation(value = "Provides the status of the requested parking slot in a location.")
    @RequestMapping(value = "/locations/{locationName}/{slotName}/_status", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationName", value = "Name of the location", required = true,
                    dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "slotName", value = "Name of the slot", required = true,
                    dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ParkingSlot.class),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public ParkingSlot getLocationSlotStatus(@PathVariable String locationName, @PathVariable String slotName) {
        for (ParkingSlot slot : service.findByLocationName(locationName).getSlots()) {
            if (slot.getName().equalsIgnoreCase(slotName)) {
                return slot;
            }
        }

        return null;
    }

    @ApiOperation(value = "Updates the parking slot information for the provided parking location.")
    @RequestMapping(value = "/locations/{locationName}", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationName", value = "Name of the location", required = true,
                    dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public void updateParkingLocation(@PathVariable String locationName,
                                      @RequestBody ParkingLocation parkingLocation) {
        service.manageSlots(parkingLocation);
    }

    @ApiOperation(value = "Delete all the parking locations.")
    @RequestMapping(value = "/locations", method = RequestMethod.DELETE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public void deleteAllLocations() {
        service.deleteAll();
    }

    @ApiOperation(value = "Delete the requested parking location.")
    @RequestMapping(value = "/locations/{locationName}", method = RequestMethod.DELETE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationName", value = "Name of the location", required = true,
                    dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deleted"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public void deleteLocation(@PathVariable String locationName) {
        service.deleteLocation(locationName);
    }

    @ApiOperation(value = "Shutdown the parking location.")
    @RequestMapping(value = "/locations/{locationName}/_shutdown", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationName", value = "Name of the location", required = true,
                    dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public void shutDownLocation(@PathVariable String locationName) {
        service.activateLocation(locationName, false);
    }

    @ApiOperation(value = "Activate the parking location.")
    @RequestMapping(value = "/locations/{locationName}/_activate", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "locationName", value = "Name of the location", required = true,
                    dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    public void activateLocation(@PathVariable String locationName) {
        service.activateLocation(locationName, true);
    }
}
