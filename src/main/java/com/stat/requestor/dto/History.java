package com.stat.requestor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class History {
    public String country;
    public Integer population;
    public Integer sqKmArea;
    public String lifeExpectancy;
    public String elevationInMeters;
    public String continent;
    public String abbreviation;
    public String location;
    public Integer iso;
    public String capitalCity;
    public JsonNode dates;

    public Long findNewVaccinated(Long currentConfirmed) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        val yesterday = LocalDate.now().minusDays(1);
        val lastConfirmed = findVaccinatedByDate(yesterday.format(formatter));
        if (!lastConfirmed.equals(currentConfirmed)) {
            return currentConfirmed - lastConfirmed;
        } else {
            val oneDayEarlier = yesterday.minusDays(1);
            return currentConfirmed - findVaccinatedByDate(oneDayEarlier.format(formatter));
        }
    }

    private Long findVaccinatedByDate(String date) {
        return dates.findValue(date).asLong();
    }
}
