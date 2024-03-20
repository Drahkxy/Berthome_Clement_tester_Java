package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

public class FareCalculatorService {

    /**
     * Permet de calculer le tarif que devra régler un utilisateur pour son ticket.
     *
     * @param ticket le ticket de l'utilisateur.
     */
    public void calculateFare(Ticket ticket) throws IllegalArgumentException{
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();

        long duration = Duration.between(inTime, outTime).toHours();

        //TODO: Some tests are failing here. Need to check if this logic is correct

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                double price = duration < 1 ? Fare.CAR_RATE_PER_HOUR * 0.75 : Fare.CAR_RATE_PER_HOUR * duration;
                ticket.setPrice(price);
                break;
            }
            case BIKE: {
                double price = duration < 1 ? Fare.BIKE_RATE_PER_HOUR * 0.75 : Fare.BIKE_RATE_PER_HOUR * duration;
                ticket.setPrice(price);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}