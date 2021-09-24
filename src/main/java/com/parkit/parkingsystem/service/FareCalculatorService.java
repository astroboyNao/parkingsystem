package com.parkit.parkingsystem.service;

import java.time.Duration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        Duration duration = Duration.between(ticket.getInTime(), ticket.getOutTime());
         
        //free if stayed for under 30 minutes
        if (duration.toMinutes() < 30 ) {
        	return;
        }
        
        float nbHours = new Float(duration.toMinutes()) /60;
        
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(nbHours * Fare.CAR_RATE_PER_HOUR );
                break;
            }
            case BIKE: {
                ticket.setPrice(nbHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}