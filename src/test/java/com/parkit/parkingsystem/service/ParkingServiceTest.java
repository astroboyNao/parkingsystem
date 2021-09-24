package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    
    
    @BeforeEach
	private void setUpPerTest() {
	    try {
	    	parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	    	//mockStatic(ParkingService.class).
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw  new RuntimeException("Failed to set up test mock objects");
	    }
	}

    @Test
    public void processExitingVehicleTest() throws Exception{
    	Ticket ticket = new Ticket();
    	ticket.setInTime(LocalDateTime.now());
    	ticket.setOutTime(LocalDateTime.now().plusHours(1));
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    	
    	ticket.setParkingSpot(parkingSpot);
    	String vehicleRegNumber = "AABBCC";
    	
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
    	when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
    	when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        
    	parkingService.processExitingVehicle();
        
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }


    @Test
    public void getNextParkingNumberIfAvailableForCar(){

    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
    	
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        
    	verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
        assertEquals(parkingSpot.getParkingType(), ParkingType.CAR);
        assertEquals(parkingSpot.isAvailable(), true);
    }

    @Test
    public void getNextParkingNumberIfAvailableForBike(){

    	when(inputReaderUtil.readSelection()).thenReturn(2);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
    	    	
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
        assertEquals(parkingSpot.getParkingType(), ParkingType.BIKE);
        assertEquals(parkingSpot.isAvailable(), true);
        
    }
    
    @Test
    public void getNextParkingNumberIfAvailableForIncorrectSelection(){

    	when(inputReaderUtil.readSelection()).thenReturn(0);
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();		
    	assertNull(parkingSpot);
        
    }
    
    @Test
    public void processIncomingVehicle() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AABBCC");
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
    	
        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }
    
    


}
