package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;


@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {
	
	private TicketDAO ticketDAO;
	
    @Mock
    public DataBaseConfig dataBaseConfig;
    
    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement stmt;

    @Mock
    private ResultSet rs;
       
    @BeforeEach
    public void setUp() {
    	ticketDAO = new TicketDAO();
    	ReflectionTestUtils.setField(ticketDAO, "dataBaseConfig", dataBaseConfig);
    }
    
    @Test
    public void saveTicket() throws Exception{
    	Ticket ticket = new Ticket();
    	ParkingSpot parkingSpot = new ParkingSpot(1,  ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(1);
        ticket.setInTime(LocalDateTime.now());
    	ticket.setHaveRecurringUser(false);
    	
    	when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        
        ticketDAO.saveTicket(ticket);
        
        verify(connection, Mockito.times(1)).prepareStatement(DBConstants.SAVE_TICKET);
    
    }

    @Test
    public void getTicket() throws Exception {
    	LocalDateTime inDateTime = LocalDateTime.now().minusHours(1);
    	LocalDateTime outDateTime = LocalDateTime.now();
    	
    	when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(Boolean.TRUE);
        when(rs.getInt(1)).thenReturn(1);
        when(rs.getString(6)).thenReturn("CAR");
        when(rs.getBoolean(7)).thenReturn(Boolean.FALSE);
        when(rs.getInt(2)).thenReturn(1);
        when(rs.getDouble(3)).thenReturn(1D);
        when(rs.getTimestamp(4)).thenReturn(Timestamp.valueOf(inDateTime));
        when(rs.getTimestamp(5)).thenReturn(Timestamp.valueOf(outDateTime));
        when(rs.getBoolean(8)).thenReturn(Boolean.FALSE);
        String vehiculeNumber ="vehiculeNumber";
        
        Ticket ticket = ticketDAO.getTicket(vehiculeNumber);
        
        assertNotNull(ticket);
        assertEquals(ticket.getVehicleRegNumber(), vehiculeNumber);
        assertNotNull(ticket.getParkingSpot());
        assertEquals(ticket.getParkingSpot().getId(), 1);
        assertEquals(ticket.getParkingSpot().getParkingType(), ParkingType.CAR);
        assertEquals(ticket.getPrice(), 1D);
        assertEquals(ticket.getInTime(), inDateTime);
        assertEquals(ticket.getOutTime(), outDateTime);
        assertEquals(ticket.getHaveRecurringUser(), Boolean.FALSE);

        verify(connection, Mockito.times(1)).prepareStatement(DBConstants.GET_LAST_TICKET);
        
    }

    @Test
    public void updateTicket() throws Exception {
    	Ticket ticket = new Ticket();
    	ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
    	ticket.setInTime(LocalDateTime.now());
    	when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        
        ticketDAO.updateTicket(ticket);
        
        verify(connection, Mockito.times(1)).prepareStatement(DBConstants.UPDATE_TICKET);
    }
}
