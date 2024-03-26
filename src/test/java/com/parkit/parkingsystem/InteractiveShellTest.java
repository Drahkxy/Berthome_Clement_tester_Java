package com.parkit.parkingsystem;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InteractiveShellTest {
	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;
	@Mock
	private static FareCalculatorService fareCalculatorService;
	@Mock
	private static ParkingService parkingService;

	private static InteractiveShell interactiveShell;

	@BeforeEach
	public void setUpInteractiveShell() {
		interactiveShell = new InteractiveShell(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService, parkingService);
	}

	@Test
	public void loadMenu_test () {
		ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;

		try {
			System.setOut(new PrintStream(outputStreamCaptor));

			interactiveShell.loadMenu();
		} finally {
			System.setOut(originalOut);
		}

		String expectedMenu = "Please select an option. Simply enter the number to choose an action\n" +
				"1 New Vehicle Entering - Allocate Parking Space\n" +
				"2 Vehicle Exiting - Generate Ticket Price\n" +
				"3 Shutdown System";

		assertEquals(expectedMenu, outputStreamCaptor.toString().trim());
	}

	@Test
	public void loadInterface_processIncomingVehicleOneTime_test() {
		when(inputReaderUtil.readSelection()).thenReturn(1, 1, 1, 3);
		when(parkingService.processIncomingVehicle()).thenReturn(true);

		interactiveShell.loadInterface();

		verify(parkingService, times(3)).processIncomingVehicle();
	}

	@Test
	public void loadInterface_processIncomingVehicleMoreThanOneTime_test() {
		when(inputReaderUtil.readSelection()).thenReturn(1, 1, 1, 1, 1, 3);
		when(parkingService.processIncomingVehicle()).thenReturn(true);

		interactiveShell.loadInterface();

		verify(parkingService, times(5)).processIncomingVehicle();
	}

	@Test
	public void loadInterface_processExitingVehicleOneTime_test() {
		when(inputReaderUtil.readSelection()).thenReturn(2, 3);

		interactiveShell.loadInterface();

		verify(parkingService, times(1)).processExitingVehicle();
		verify(parkingService, times(0)).processIncomingVehicle();
	}

	@Test
	public void loadInterface_processExitingVehicleMoreThanOneTime_test() {
		when(inputReaderUtil.readSelection()).thenReturn(2, 2, 2, 2, 2, 3);

		interactiveShell.loadInterface();

		verify(parkingService, times(5)).processExitingVehicle();
		verify(parkingService, times(0)).processIncomingVehicle();
	}

	@Test
	public void loadInterface_existSystem_test() {
		when(inputReaderUtil.readSelection()).thenReturn(3);

		interactiveShell.loadInterface();

		verify(parkingService, times(0)).processExitingVehicle();
		verify(parkingService, times(0)).processIncomingVehicle();
	}

	@Test
	public void loadInterface_withInvalidInput_test() {
		when(inputReaderUtil.readSelection()).thenReturn(4, 3);

		interactiveShell.loadInterface();

		verify(parkingService, times(0)).processExitingVehicle();
		verify(parkingService, times(0)).processIncomingVehicle();
	}

}
