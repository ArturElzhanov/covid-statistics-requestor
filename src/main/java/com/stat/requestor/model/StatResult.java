package com.stat.requestor.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StatResult {
    private Long confirmed;
    private Long recovered;
    private Long deaths;
    private Double vaccinated;
    private Long newConfirmed;

    @Override
    public String toString() {
        return "confirmed=" + confirmed + "\n" +
                "recovered=" + recovered + "\n" +
                "deaths=" + deaths + "\n" +
                "vaccinated=" + vaccinated + "\n" +
                "newConfirmed=" + newConfirmed;
    }
}
