package com.parkit.parkingsystem.service;

import java.time.Duration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private static final int firstFreeMinutes = 30;
    private static final double discountForRecurringUsers = 0.5;

	public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
         
        //free if stayed for under 30 minutes
        if (isFree(duration) ) {
        	return;
        }
        
        float nbHours = new Float(duration.toMinutes()) /60;
        
        double discount = getDiscount(ticket);
        
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(nbHours * ( Fare.CAR_RATE_PER_HOUR - discount ));
                break;
            }
            case BIKE: {
                ticket.setPrice(nbHours * ( Fare.BIKE_RATE_PER_HOUR - discount ));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

	//When the user exits the parking garage, they will benefit from a 5% discount on the normal fee
	private double getDiscount(Ticket ticket) {
		return (ticket.getId() > 0 ) ? discountForRecurringUsers : 0;
	}

    //free if stayed for under 30 minutes
	private boolean isFree(Duration duration) {
		return duration.toMinutes() < firstFreeMinutes;
	}
}