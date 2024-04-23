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

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

//    private void setUpGetTicket(){
//        when(ticketDAO.getTicket(anyString())).thenAnswer(invocation -> {
//            String arg = invocation.getArgument(0);
//            if (arg.equals("ABCDEF")){
//                ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
//
//                Ticket ticket = new Ticket();
//                ticket.setInTime(LocalDateTime.now().minusHours(1));
//                ticket.setParkingSpot(parkingSpot);
//                ticket.setVehicleRegNumber("ABCDEF");
//                return ticket;
//            } else {
//                return null;
//            }
//        });
//    }

    private void setUpGetNbTicket(boolean incomingVehicle){
        when(ticketDAO.getNbTicket(anyString())).then(invocation -> {
            String arg = invocation.getArgument(0);

            int validNb = incomingVehicle ? 1 : 2;

            return arg.equals("ABCDEF") ? validNb : (validNb - 1);
        });
    }

    /*@ParameterizedTest(name = "Vehicle with {0} registration number was already registered -> {1}")
    @CsvSource({"ABCDEF, true", "GHIJKL, false"})
    public void verifyIfVehicleAlreadyKnown_forIncomingVehicle_withValidAndInvalidInput_test(String vehicleRegNumber, boolean expectedResult) {
        setUpGetNbTicket(true);

        boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(vehicleRegNumber, true);

        System.out.printf("%s expected result and got %s.%n", expectedResult, alreadyRegister);

        verify(ticketDAO, times(1)).getNbTicket(vehicleRegNumber);

        //assertEquals(expectedResult, alreadyRegister);
        assertThat(alreadyRegister).isEqualTo(expectedResult);
    }*/

     @Test
     public void verifyIfVehicleAlreadyKnown_forIncomingVehicle_withAlreadyKnownVehicle_test() {
         setUpGetNbTicket(true);
         String alreadyKnownVehicleRegNumber = "ABCDEF";

         boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(alreadyKnownVehicleRegNumber, true);

         verify(ticketDAO, times(1)).getNbTicket(alreadyKnownVehicleRegNumber);

         assertThat(alreadyRegister).isTrue();
     }

    @Test
    public void verifyIfVehicleAlreadyKnown_forIncomingVehicle_withUnknownVehicle_test() {
        setUpGetNbTicket(true);
        String newVehicleRegNumber = "GHIJKL";

        boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(newVehicleRegNumber, true);

        verify(ticketDAO, times(1)).getNbTicket(newVehicleRegNumber);

        assertThat(alreadyRegister).isFalse();
    }

    /*@ParameterizedTest(name = "Vehicle with {0} registration number was already registered -> {1}")
    @CsvSource({"ABCDEF, true", "GHIJKL, false"})
    public void verifyIfVehicleAlreadyKnown_forExitingVehicle_withValidAndInvalidInput_test(String vehicleRegNumber, boolean expectedResult) {
        setUpGetNbTicket(false);

        boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(vehicleRegNumber, false);

        System.out.printf("%s expected result and got %s.%n", expectedResult, alreadyRegister);

        verify(ticketDAO, times(1)).getNbTicket(vehicleRegNumber);

        assertEquals(expectedResult, alreadyRegister);
    }*/

    @Test
    public void verifyIfVehicleAlreadyKnown_forExitingVehicle_withAlreadyKnownVehicle_test() {
         setUpGetNbTicket(false);
         String alreadyKnownVehicleRegNumber = "ABCDEF";

         boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(alreadyKnownVehicleRegNumber, false);

         verify(ticketDAO, times(1)).getNbTicket(alreadyKnownVehicleRegNumber);

         assertThat(alreadyRegister).isTrue();
    }

    @Test
    public void verifyIfVehicleAlreadyKnown_forExitingVehicle_withUnknownVehicle_test() {
        setUpGetNbTicket(false);
        String newVehicleRegNumber = "GHIJKL";

        boolean alreadyRegister = parkingService.verifyIfVehicleAlreadyKnown(newVehicleRegNumber, false);

        verify(ticketDAO, times(1)).getNbTicket(newVehicleRegNumber);

        assertThat(alreadyRegister).isFalse();
    }

    @ParameterizedTest(name = "Vehicle registration number {0} successfully recovered ")
    @ValueSource(strings = {"ABCDEF", "GHIJKL", "MNOPQR"})
    public void getVehicleRegNumber_withValidInput_test(String vehicleRegNumber) throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

        String vrn = parkingService.getVehicleRegNumber();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();

        assertThat(vrn).isEqualTo(vehicleRegNumber);
    }

    @Test
    public void getVehicleRegNumber_withInvalidInput_test() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(new Exception("Invalid input provided"));

        assertThatThrownBy(() -> parkingService.getVehicleRegNumber()).isInstanceOf(Exception.class);

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
    }

    @ParameterizedTest(name = "{0} type successfully retrieved")
    @CsvSource({"CAR, 1", "BIKE, 2"})
    public void getVehicleType_withValidInput_test(String vehicleName, int vehicleType) {
        when(inputReaderUtil.readSelection()).thenReturn(vehicleType);

        ParkingType parkingType = parkingService.getVehicleType();

        verify(inputReaderUtil, times(1)).readSelection();

        assertThat(parkingType).isEqualTo(ParkingType.getParkingTypeByInt(vehicleType));
    }

    @Test
    public void getVehicleType_withInvalidInput_test() {
        Random r = new Random();

        when(inputReaderUtil.readSelection()).thenReturn(r.nextInt() + 3);

        assertThatThrownBy(() -> parkingService.getVehicleType()).isInstanceOf(IllegalArgumentException.class);

        verify(inputReaderUtil, times(1)).readSelection();
    }

    @Test
    public void getNextParkingNumberIfAvailable_withFreeSlot_test() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertThat(parkingSpot).isNotNull();

        verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
    }

    @Test
    public void getNextParkingNumberIfAvailable_withoutFreeSlot_test() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertThat(parkingSpot).isNull();

        verify(inputReaderUtil, times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
    }

    @Test
    public void processExitingVehicle_withValidUserInput_test() throws Exception {
        String regNumber = "ABCD";
        Ticket ticket = mock(Ticket.class);
        ParkingSpot parkingSpot = mock(ParkingSpot.class);

        when(ticket.getParkingSpot()).thenReturn(parkingSpot);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
        when(ticketDAO.getTicket(regNumber)).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);

        parkingService.processExitingVehicle();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).getTicket(regNumber);
        verify(ticketDAO, times(1)).updateTicket(ticket);
        verify(parkingSpotDAO, times(1)).updateParking(parkingSpot);
    }

    @Test
    public void processExitingVehicle_withInvalidUserInput_test() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(Exception.class);

        parkingService.processExitingVehicle();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(0)).getTicket(any());
        verify(ticketDAO, times(0)).updateTicket(any());
        verify(parkingSpotDAO, times(0)).updateParking(any());
    }

    @Test
    public void processExitingVehicle_withTicketUpdateFail_test() throws Exception {
        String regNumber = "ABCD";
        Ticket ticket = mock(Ticket.class);

        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
        when(ticketDAO.getTicket(regNumber)).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(false);

        parkingService.processExitingVehicle();

        verify(inputReaderUtil, times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).getTicket(regNumber);
        verify(ticketDAO, times(1)).updateTicket(ticket);
        verify(parkingSpotDAO, times(0)).updateParking(any());
    }

    @Test
    public void processIncomingVehicle_withSlotAvailable_test() throws Exception {
        String regNumber = "ABCD";

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
        when(ticketDAO.getNbTicket(regNumber)).thenReturn(0);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processIncomingVehicle();

        verify(inputReaderUtil,times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(inputReaderUtil,times(1)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(1)).getNbTicket(anyString());
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
    }

    @Test
    public void processIncomingVehicle_withNoSlotsAvailable_test() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

        parkingService.processIncomingVehicle();

        verify(inputReaderUtil,times(1)).readSelection();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(inputReaderUtil,times(0)).readVehicleRegistrationNumber();
        verify(ticketDAO, times(0)).getNbTicket(anyString());
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(0)).saveTicket(any(Ticket.class));
    }

}
