package com.stat.requestor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vaccines {
    public Long administered;
    public Double peopleVaccinated;
    public Long peoplePartiallyVaccinated;
    public String country;
    public Double population;
    public Integer sqKmArea;
    public String lifeExpectancy;
    public String elevationInMeters;
    public String continent;
    public String abbreviation;
    public String location;
    public Integer iso;
    public String capitalCity;
    public String updated;

    public Double vaccinatedPercentage() {
        return peopleVaccinated/population * 100;
    }
}
