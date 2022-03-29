package com.medicensoya.coronavirustracker.models;

import lombok.Data;

@Data
public class LocationData {

    private String state;
    private String country;
    private int latestTotalCases;
    private int differenceFromPreviousDay;
}
