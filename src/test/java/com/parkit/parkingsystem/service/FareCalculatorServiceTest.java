package com.parkit.parkingsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorServiceTest {

    private FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeEach
    private void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
	public void calculateFareBikeWithFutureInTime(){
	    LocalDateTime inTime = LocalDateTime.now();
	    inTime = inTime.plusHours(1);
	    LocalDateTime outTime = LocalDateTime.now();
	    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
	
	    ticket.setInTime(inTime);
	    ticket.setOutTime(outTime);
	    ticket.setParkingSpot(parkingSpot);
	    
	    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}
    
    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusMinutes(45);
    	LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusMinutes(45);
    	LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusHours(24);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }


    @Test
    public void calculateFareBikeWithLessThan30MinutesParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusMinutes(29);
    	LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals(0, ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThan30MinutesParkingTime(){
    	LocalDateTime inTime = LocalDateTime.now();
    	inTime = inTime.minusMinutes(29);
    	LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals(0, ticket.getPrice() );
    }



    @Test
	public void calculateFareBikeWithDiscount(){
	    LocalDateTime inTime = LocalDateTime.now();
	    inTime = inTime.minusHours(1);
	    LocalDateTime outTime = LocalDateTime.now();
	    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
	    ticket.setId(1);
	    ticket.setInTime(inTime);
	    ticket.setOutTime(outTime);
	    ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);
        
        assertEquals( (1 * ( Fare.BIKE_RATE_PER_HOUR - Fare.DISCOUNT )) , ticket.getPrice());
	}
    

    @Test
	public void calculateFareCarWithDiscount(){
	    LocalDateTime inTime = LocalDateTime.now();
	    inTime = inTime.minusHours(1);
	    LocalDateTime outTime = LocalDateTime.now();
	    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
	    ticket.setId(1);
	    ticket.setInTime(inTime);
	    ticket.setOutTime(outTime);
	    ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);
        
        assertEquals( (1 * ( Fare.CAR_RATE_PER_HOUR - Fare.DISCOUNT )) , ticket.getPrice());
	}
}
