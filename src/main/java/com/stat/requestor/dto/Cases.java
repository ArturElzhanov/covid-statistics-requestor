package com.stat.requestor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.stat.requestor.model.StatResult;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cases {
    private Long confirmed;
    private Long recovered;
    private Long deaths;
    private String country;
    private Long population;
    private Long sqKmArea;
    private String lifeExpectancy;
    private String elevationInMeters;
    private String continent;
    private String abbreviation;
    private String location;
    private Integer iso;
    private String capitalCity;
    @JsonAlias("lat")
    private String latitude;
    @JsonAlias("long")
    private String longitude;
    private String updated;

    public StatResult toResult() {
       return new StatResult()
               .setConfirmed(confirmed)
               .setRecovered(recovered)
               .setDeaths(deaths);
    }
}

