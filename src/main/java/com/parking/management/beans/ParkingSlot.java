/**
 *
 */
package com.parking.management.beans;

import org.springframework.data.annotation.Id;

import java.util.Comparator;

public class ParkingSlot {

    public ParkingSlot() {
    }

    public ParkingSlot(String name, String status, long ownerId) {
        super();
        this.name = name;
        this.status = status;
        this.ownerId = ownerId;
    }

    @Id
    private String id;
    private String name;
    private String status;
    private long ownerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public static Comparator<ParkingSlot> ParkingSlotNameComparator
            = new Comparator<ParkingSlot>() {

        public int compare(ParkingSlot slot1, ParkingSlot slot2) {

            String parkingSlot1 = slot1.getName().replaceAll("[^0-9]", "");
            String parkingSlot2 = slot2.getName().replaceAll("[^0-9]", "");

            if (parkingSlot1.isEmpty() || parkingSlot2.isEmpty()) {
                //ascending order
                return parkingSlot1.compareTo(parkingSlot2);
                //descending order
                //return parkingSlot1.compareTo(parkingSlot2);
            } else {
                Integer number1 = Integer.parseInt(parkingSlot1);
                Integer number2 = Integer.parseInt(parkingSlot2);
                return number1.compareTo(number2);
            }
        }

    };
}
