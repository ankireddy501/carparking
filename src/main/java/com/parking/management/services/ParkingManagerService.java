package com.parking.management.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parking.management.beans.ParkingLocation;
import com.parking.management.beans.ParkingSlot;
import com.parking.management.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParkingManagerService {

	private final static String AVAILABLE = "available";

    @Autowired
    ParkingRepository repository;
	
	public ParkingManagerService() {}
	
	public void manageSlots(ParkingLocation location)
	{
		if(repository.countDistinctParkingLocationByName(location.getName()) != 0)
		{
			ParkingLocation oldLocation = repository.findDistinctParkingLocationByName(location.getName());
			
			// NOTE: ignoring lattitude and longitude updates..
			if(oldLocation.getSlots().size()!=0)
			{
				// compare and update..
				for(ParkingSlot newSlot: location.getSlots())
				{
					ParkingSlot updatableSlot = null;
					for(ParkingSlot oldSlot: oldLocation.getSlots())
					{
						if(newSlot.getName().equals(oldSlot.getName()))
						{
							updatableSlot = oldSlot;
							break;
						}
					}
					
					if(updatableSlot != null)
					{
						updatableSlot.setStatus(newSlot.getStatus());
						updatableSlot.setOwnerId(newSlot.getOwnerId());
					}
					else
					{
						oldLocation.getSlots().add(new ParkingSlot(newSlot.getName(),
                                newSlot.getStatus(), newSlot.getOwnerId()));
					}
				}
			}
			else
            {
				oldLocation.getSlots().addAll(getSlots(location.getSlots()));
			}

            repository.save(oldLocation);
		}
		else
        {
            repository.save(location);
		}

	}

	private List<ParkingSlot> getSlots(List<ParkingSlot> slots) {
		List<ParkingSlot> list = new ArrayList<>();
		
		ParkingSlot slot;
		for(ParkingSlot parkingSlot: slots)
		{
			 slot = new ParkingSlot(parkingSlot.getName(), parkingSlot.getStatus(),
                     parkingSlot.getOwnerId());
			 list.add(slot);
		}
		
		return list;
	}
	
	public Integer getAvailabilityForSlots(List<ParkingSlot> slots)
	{
		int availablity = 0;
		
		if(slots.size() == 0) 
			return null;
		
		for(ParkingSlot slot: slots)
		{
			if(slot.getStatus().equals(AVAILABLE))
			{
				availablity++;
			}
		}
		
		return availablity;
	}

    public List<ParkingLocation> findAllLocation() {
        return repository.findAll();
    }

    public ParkingLocation findByLocationName(String name) {
        return repository.findDistinctParkingLocationByName(name);
    }
}
