package com.parking.management.services;

import com.parking.management.beans.ParkingLocation;
import com.parking.management.beans.ParkingSlot;
import com.parking.management.externalservices.CollaborationAdapter;
import com.parking.management.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingManagerService {

	private final static String AVAILABLE = "available";

    @Autowired
    ParkingRepository repository;

    @Autowired
    CollaborationAdapter collaborationAdapter;
	
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
                        if (!updatableSlot.getStatus().equals(newSlot.getStatus())) {
                            collaborationAdapter.postUpdateToFeed(location.getName(), newSlot.getName(),
                                    !newSlot.getStatus().equals(AVAILABLE));
                        }
					}
					else
					{
						oldLocation.getSlots().add(new ParkingSlot(newSlot.getName(),
                                newSlot.getStatus(), newSlot.getOwnerId()));
                        collaborationAdapter.postUpdateToFeed(location.getName(), newSlot.getName(),
                                !newSlot.getStatus().equals(AVAILABLE));
					}
				}
			}
			else
            {
                List<ParkingSlot> slots = getSlots(location.getSlots());
                oldLocation.getSlots().addAll(slots);
                for (ParkingSlot slot : slots) {
                    collaborationAdapter.postUpdateToFeed(location.getName(), slot.getName(),
                            !slot.getStatus().equals(AVAILABLE));
                }
			}

            repository.save(oldLocation);
		}
		else
        {
            repository.save(location);
            for (ParkingSlot slot : location.getSlots()) {
                collaborationAdapter.postUpdateToFeed(location.getName(), slot.getName(),
                        !slot.getStatus().equals(AVAILABLE));
            }
		}
	}

	private List<ParkingSlot> getSlots(List<ParkingSlot> slots) {
		List<ParkingSlot> list = new ArrayList();
		
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

    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteLocation(String name) {
        ParkingLocation location = repository.findDistinctParkingLocationByName(name);
        repository.delete(location.getId());
    }

    public void activateLocation(String name, boolean active) {
        ParkingLocation location = repository.findDistinctParkingLocationByName(name);
        location.setActive(active);
        repository.save(location);
    }
}
