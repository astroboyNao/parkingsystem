package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.BeforeAll;
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

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

	private ParkingSpotDAO parkingSpotDao;
	
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
    	parkingSpotDao = new ParkingSpotDAO();
    	ReflectionTestUtils.setField(parkingSpotDao, "dataBaseConfig", dataBaseConfig);
    }
     
    @Test
    public void getNextAvailableSlotTest() throws Exception{
    	when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);
        when(stmt.executeQuery()).thenReturn(rs);
        
        int availableSlot = parkingSpotDao.getNextAvailableSlot(ParkingType.BIKE);
        
        assertEquals(availableSlot, 1);
        verify(connection, Mockito.times(1)).prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
    }
    
    @Test
    public void updateParking() throws Exception{

        ParkingSpot parkingSpot = new ParkingSpot(1,  ParkingType.CAR, true);
        
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);
        
        boolean isUpdated = parkingSpotDao.updateParking(parkingSpot);
        
        assertTrue(isUpdated);
        verify(connection, Mockito.times(1)).prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
        
            
    }

}
