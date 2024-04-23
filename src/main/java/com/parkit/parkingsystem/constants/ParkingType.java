package com.parkit.parkingsystem.constants;

public enum ParkingType {
    CAR,
    BIKE;

    public static ParkingType getParkingTypeByInt(int type) {
        switch (type) {
            case 1 : return CAR;
            case 2 : return BIKE;
            default: return null;
        }
    }
}
