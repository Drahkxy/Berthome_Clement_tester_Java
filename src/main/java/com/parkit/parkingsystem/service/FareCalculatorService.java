package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) throws IllegalArgumentException{
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();

        Duration duration = Duration.between(inTime, outTime);

        if (duration.toMinutes() < 30) {

            ticket.setPrice(0);

        } else {
            double price;

            switch (ticket.getParkingSpot().getParkingType()){
                case CAR: {
                    if (duration.toMinutes() < 60)
                        price = Fare.CAR_RATE_PER_HOUR * 0.75;
                    else
                        price = Fare.CAR_RATE_PER_HOUR * duration.toHours();
                    break;
                }
                case BIKE: {
                    if (duration.toMinutes() < 60)
                        price = Fare.BIKE_RATE_PER_HOUR * 0.75;
                    else
                        price = Fare.BIKE_RATE_PER_HOUR * duration.toHours();
                    break;
                }
                default: throw new IllegalArgumentException("Unkown Parking Type");
            }

            ticket.setPrice(discount ? (price * 0.95) : price);
        }
    }

    public void calculateFare(Ticket ticket){
        calculateFare(ticket, false);
    }

    public void test () {
        System.out.println("CA marcjhe pa iodskkjfk");
    }

}