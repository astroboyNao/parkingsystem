package com.parkit.parkingsystem.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.ParkingSpotDAOTest;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.dao.TicketDAOTest;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;
    private DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeEach
    private void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        
        //check that a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket);
        assertNotNull(ticket.getParkingSpot());
        assertNotNull(ticket.getInTime());
        assertEquals(ticket.getParkingSpot().getParkingType(), ParkingType.CAR);
        
        int nbSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        
        //check non dispo
        assertEquals(2, nbSlot);
    }

    @Test
    public void testParkingLotExit() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        
        parkingService.processExitingVehicle();

        //check that the fare generated and out time are populated correctly in the database
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getOutTime());
        assertNotNull(ticket.getPrice());
        assertTrue(ticket.getParkingSpot().isAvailable());
        
    }
    

    @Test
    public void test30minFreeParking() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        parkingService.processIncomingVehicle();
        
        Ticket firstTicket = ticketDAO.getTicket("ABCDEF");
        firstTicket.setInTime(LocalDateTime.now().minusMinutes(29));
        firstTicket.setOutTime(LocalDateTime.now());
        ticketDAO.updateTicket(firstTicket);
        
        parkingService.processExitingVehicle();
        
        //check is free for first 30 minutes
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals(0D, ticket.getPrice());
    }
    

    @Test
    public void testPayedAfter30minParking() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        parkingService.processIncomingVehicle();
        
        Ticket firstTicket = ticketDAO.getTicket("ABCDEF");
        firstTicket.setInTime(LocalDateTime.now().minusMinutes(30));
        ticketDAO.updateTicket(firstTicket);
        
        parkingService.processExitingVehicle();
        
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getPrice());
    }
    
    
    @Test
    public void testRecurringUsers() throws Exception{
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        parkingService.processIncomingVehicle();
        
        Ticket firstTicket = ticketDAO.getTicket("ABCDEF");
        firstTicket.setInTime(LocalDateTime.now().minusHours(2));
        ticketDAO.saveTicket(firstTicket);
        
        parkingService.processExitingVehicle();
        firstTicket = ticketDAO.getTicket("ABCDEF");
        
        Ticket secoundTicket = ticketDAO.getTicket("ABCDEF");
        secoundTicket.setInTime(LocalDateTime.now().minusHours(1));
        ticketDAO.updateTicket(secoundTicket);
        
        parkingService.processExitingVehicle();
        secoundTicket = ticketDAO.getTicket("ABCDEF");
        
        assertEquals(firstTicket.getPrice() - Fare.DISCOUNT * firstTicket.getPrice(), secoundTicket.getPrice());
    }


}
