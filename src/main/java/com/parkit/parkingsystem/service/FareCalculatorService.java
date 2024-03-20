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

        Duration duration = Duration.between(inTime, outTime);


        //TODO: Some tests are failing here. Need to check if this logic is correct


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                double price;

                if (duration.toMinutes() < 30)
                    price = 0;
                else if (duration.toMinutes() < 60)
                    price = Fare.CAR_RATE_PER_HOUR * 0.75;
                else
                    price = Fare.CAR_RATE_PER_HOUR * duration.toHours();

                ticket.setPrice(price);
                break;
            }
            case BIKE: {
                double price;

                if (duration.toMinutes() < 30)
                    price = 0;
                else if (duration.toMinutes() < 60)
                    price = Fare.BIKE_RATE_PER_HOUR * 0.75;
                else
                    price = Fare.BIKE_RATE_PER_HOUR * duration.toHours();

                ticket.setPrice(price);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}