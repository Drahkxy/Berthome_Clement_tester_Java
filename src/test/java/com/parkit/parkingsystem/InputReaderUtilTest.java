package com.parkit.parkingsystem;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InputReaderUtilTest {
	private InputReaderUtil inputReaderUtil;

	@Mock
	private Scanner scanner;

	@BeforeEach
	public void setUp () {
		inputReaderUtil = new InputReaderUtil(scanner);
	}

	@ParameterizedTest
	@CsvSource({"1, 1", "2, 2", "test, -1"})
	public void readSelection_withValidAndInvalidInput_test(String input, int resultExpected) {
		when(scanner.nextLine()).thenReturn(input);

		int result = inputReaderUtil.readSelection();

		assertEquals(resultExpected, result);

		verify(scanner, times(1)).nextLine();
	}

	@Test
	public void readVehicleRegistrationNumber_withValidInput_test(){
		when(scanner.nextLine()).thenReturn("AB-123-CD");

		try {
			String result = inputReaderUtil.readVehicleRegistrationNumber();

			assertEquals("AB-123-CD", result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		verify(scanner, times(1)).nextLine();
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " ", "\t", "\n"})
	@NullSource
	public void readVehicleRegistrationNumber_withInvalidInput_test(String input){
		when(scanner.nextLine()).thenReturn(input);

		assertThrows(Exception.class, () -> inputReaderUtil.readVehicleRegistrationNumber());

		verify(scanner, times(1)).nextLine();
	}

}
