package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    @Mock
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }


    @ParameterizedTest(name = "Calculation of the fare for a car slot and {0} {1}")
    @CsvSource({"1, hour", "5, hours", "10, hours", "24, hours", "48, hours"})
    public void calculateFareCar_forAnHourAndMoreParkingTime_test(int hours, String txt){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusHours(hours);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        //assertEquals((Fare.CAR_RATE_PER_HOUR * hours), ticket.getPrice());
        assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * hours);
    }


    @ParameterizedTest(name = "Calculation of the fare for a bike slot and {0} {1}")
    @CsvSource({"1, hour", "5, hours", "10, hours", "24, hours", "48, hours"})
    public void calculateFareBike_forAnHourAndMoreParkingTime_test(int hours, String txt){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusHours(hours);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        //assertEquals((Fare.BIKE_RATE_PER_HOUR * hours), ticket.getPrice());
        assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * hours);
    }


    @Test
    public void calculateFareBike_withLessThanOneHourAndMoreThanThirtyMinutesParkingTime_test(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        //assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
        assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
    }


    @Test
    public void calculateFareCar_withLessThanOneHourAndMoreThanThirtyMinutesParkingTime_test(){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(45);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        //assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
        assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    }


    @ParameterizedTest(name = "Calculation of the discount fare for a car slot and {0} {1}")
    @CsvSource({"0.75, hour", "1, hour", "5, hours", "24, hours", "48, hours"})
    public void calculateFareCar_withDiscount_test(double hours, String txt){
        int minutes = Double.valueOf(60 * hours).intValue();

        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(minutes);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket, true);

        double expectedFare = 0.95 * (hours < 1 ? (0.75 * Fare.CAR_RATE_PER_HOUR) : (Fare.CAR_RATE_PER_HOUR * hours));

        //assertEquals(expectedFare, ticket.getPrice());
        assertThat(ticket.getPrice()).isEqualTo(expectedFare);
    }


    @ParameterizedTest(name = "Calculation of the discount fare for a bike slot and {0} {1}")
    @CsvSource({"0.75, hour", "1, hour", "5, hours", "24, hours", "48, hours"})
    public void calculateFareBike_withDiscount_test(double hours, String txt){
        int minutes = Double.valueOf(60 * hours).intValue();

        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(minutes);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket, true);

        double expectedFare = 0.95 * (hours < 1 ? (0.75 * Fare.BIKE_RATE_PER_HOUR) : (Fare.BIKE_RATE_PER_HOUR * hours));

        //assertEquals(expectedFare, ticket.getPrice());
        assertThat(ticket.getPrice()).isEqualTo(expectedFare);
    }


    @ParameterizedTest(name = "Calculation of the fare for a {1} with less than 30 minutes parking time")
    @CsvSource({"1, car", "2, bike"})
    public void calculateFare_withLessThanThirtyMinutesParkingTime_test(int parkingType, String parkingTypeTxt){
        ParkingType pt = ParkingType.getParkingTypeByInt(parkingType);

        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusMinutes(15);
        ParkingSpot parkingSpot = new ParkingSpot(1, pt,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        //assertEquals(0, ticket.getPrice() );
        assertThat(ticket.getPrice()).isEqualTo(0);
    }


    @ParameterizedTest(name = "Calculation of the fare for a {1} with future InTimes")
    @CsvSource({"1, car", "2, bike"})
    public void calculateFare_withFutureInTime_test(int parkingType, String parkingTypeTxt){
        ParkingType pt = ParkingType.getParkingTypeByInt(parkingType);

        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, pt,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
        assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void calculateFare_forUnknownType_test (){
        LocalDateTime outTime = LocalDateTime.now();
        LocalDateTime inTime = outTime.minusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
        assertThatThrownBy(() -> fareCalculatorService.calculateFare(ticket)).isInstanceOf(NullPointerException.class);
    }

}
