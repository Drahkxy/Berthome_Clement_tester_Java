package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    private static FareCalculatorService fareCalculatorService;


    @BeforeEach
    public void setUpPerTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);
    }


    private void setUpGetTicket(){
        when(ticketDAO.getTicket(anyString())).thenAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            if (arg.equals("ABCDEF")){
                ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

                Ticket ticket = new Ticket();
                ticket.setInTime(LocalDateTime.now().minusHours(1));
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber("ABCDEF");
                return ticket;
            } else {
                return null;
            }
        });
    }

    private void setUpGetNbTicket(boolean incomingVehicle){
        when(ticketDAO.getNbTicket(anyString())).thenAnswer(invocation -> {
            String arg = invocation.getArgument(0);

            int validNb = incomingVehicle ? 1 : 2;

            return arg.equals("ABCDEF") ? validNb : (validNb - 1);
        });
    }



    @ParameterizedTest(name = "Vehicle with {0} registration number was already registered -> {1}")
    @CsvSource({"ABCDEF, true", "GHIJKL, false"})
    public void verifyIfVehicleAlreadyKnown_forIncomingVehicle_withValidAndInvalidInput_test(String vehicleRegNumber, boolean expectedResult) {
        setUpGetNbTicket(true);

        boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(vehicleRegNumber, true);

        System.out.printf("%s expected result and got %s.%n", expectedResult, alreadyRegister);

        verify(ticketDAO, times(1)).getNbTicket(anyString());

        assertEquals(expectedResult, alreadyRegister);
    }

    @ParameterizedTest(name = "Vehicle with {0} registration number was already registered -> {1}")
    @CsvSource({"ABCDEF, true", "GHIJKL, false"})
    public void verifyIfVehicleAlreadyKnown_forExitingVehicle_withValidAndInvalidInput_test(String vehicleRegNumber, boolean expectedResult) {
        setUpGetNbTicket(false);

        boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(vehicleRegNumber, false);

        System.out.printf("%s expected result and got %s.%n", expectedResult, alreadyRegister);

        verify(ticketDAO, times(1)).getNbTicket(anyString());

        assertEquals(expectedResult, alreadyRegister);
    }

    @ParameterizedTest(name = "Vehicle registration number {0} successfully recovered ")
    @ValueSource(strings = {"ABCDEF", "GHIJKL", "MNOPQR"})
    public void getVehicleRegNumber_withValidInput_test(String vehicleRegNumber) throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

        String vrn = parkingService.getVehicleRegNumber();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();

        assertEquals(vehicleRegNumber, vrn);
    }

    @Test
    public void getVehicleRegNumber_withInvalidInput_test() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(new Exception("Invalid input provided"));

        assertThrows(Exception.class, () -> parkingService.getVehicleRegNumber());

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
    }

    @ParameterizedTest(name = "{0} type successfully retrived")
    @CsvSource({"CAR, 1", "BIKE, 2"})
    public void getVehicleType_withValidInput_test(String vehicleName, int vehicleType) {
        if (vehicleType == 1 || vehicleType == 2) {
            when(inputReaderUtil.readSelection()).thenReturn(vehicleType);
        } else {
            when(inputReaderUtil.readSelection()).thenReturn(-1);
        }

        ParkingType parkingType = parkingService.getVehicleType();

        verify(inputReaderUtil, times(1)).readSelection();

        assertEquals(ParkingType.getParkingTypeByInt(vehicleType), parkingType);
    }

    @Test
    public void getVehicleType_withInvalidInput_test() {
        Random r = new Random();
        when(inputReaderUtil.readSelection()).thenReturn(r.nextInt() + 3);

        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehicleType());


        verify(inputReaderUtil, times(1)).readSelection();
    }

    @Test
    public void getNextParkingNumberIfAvailable_withFreeSlot_test(){
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));

        assertNotNull(parkingSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailable_withoutFreeSlot_test(){
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));

        assertNull(parkingSpot);
    }

    @Test
    public void getNextParkingNumberIfAvailable_withWrongUserInput_test() {
        Random r = new Random();
        when(inputReaderUtil.readSelection()).thenReturn(r.nextInt() + 3);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(0)).getNextAvailableSlot(any(ParkingType.class));

        assertNull(parkingSpot);
    }

    @ParameterizedTest(name = "Vehicle with {0} registration number entered.")
    @ValueSource(strings = {"ABCDEF", "HIJQLM"})
    public void processIncomingVehicle_withAvailableParkingSpot_test(String vehicleRegNumber) throws Exception {
        setUpGetNbTicket(true);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        boolean result = parkingService.processIncomingVehicle();

        verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).getNbTicket(anyString());
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));

        assertTrue(result);
    }

    @Test
    public void processIncomingVehicle_withUnavailableParkingSpot_test() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        boolean result = parkingService.processIncomingVehicle();

        verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);

        verify(inputReaderUtil, times(0)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(0)).getNbTicket(anyString());
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(0)).saveTicket(any(Ticket.class));

        assertFalse(result);
    }

    @Test
    public void processExitingVehicle_withValidRegNumber_test() throws Exception {
        setUpGetTicket();
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        doNothing().when(fareCalculatorService).calculateFare(any(Ticket.class));

        boolean result = parkingService.processExitingVehicle();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).getTicket("ABCDEF");
        verify(ticketDAO, times(1)).getNbTicket("ABCDEF");
        verify(fareCalculatorService, times(1)).calculateFare(any(Ticket.class));
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));

        assertTrue(result);
    }

    @Test
    public void processExitingVehicle_withInvalidRegNumber_test() throws Exception {
        setUpGetTicket();
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("GHIJKL");

        boolean result = parkingService.processExitingVehicle();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).getTicket("GHIJKL");

        verify(ticketDAO, times(0)).getNbTicket(anyString());
        verify(fareCalculatorService, times(0)).calculateFare(any(Ticket.class));
        verify(ticketDAO, times(0)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));

        assertFalse(result);
    }

    @Test
    public void processExitingVehicle_withoutTicketUpdate_test() throws Exception {
        setUpGetTicket();
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        doNothing().when(fareCalculatorService).calculateFare(any(Ticket.class));

        boolean result = parkingService.processExitingVehicle();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).getTicket("ABCDEF");
        verify(ticketDAO, times(1)).getNbTicket(anyString());
        verify(fareCalculatorService, times(1)).calculateFare(any(Ticket.class));
        verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));

        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));

        assertFalse(result);
    }

}
