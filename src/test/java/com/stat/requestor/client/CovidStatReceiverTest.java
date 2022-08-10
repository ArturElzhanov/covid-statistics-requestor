package com.stat.requestor.client;


import com.stat.requestor.exception.ClientException;
import com.stat.requestor.exception.ResourceNotFoundException;
import com.stat.requestor.exception.ServerException;
import lombok.val;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CovidStatReceiverTest {

    private static final String TEST_COUNTRY = "France";
    public static final Long EXPECTED_CONFIRMED = 33191512L;
    public static final Long EXPECTED_RECOVERED = 110L;
    public static final Long EXPECTED_DEATHS = 149420L;
    public static final Double VACCINATED = 53009757d;
    public static final Double POPULATION = 64979548d;
    public static final Long BEFORE_CONFIRMED = 33153696L;

    private final MockWebServer server = new MockWebServer();

    @Test
    public void shouldReceiveStatisticsByCountry() throws IOException {
        Dispatcher mDispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().contains("/cases")) {
                    return new MockResponse().setBody(String.format("{\n" +
                            "    \"All\": {\n" +
                            "        \"confirmed\": %s,\n" +
                            "        \"recovered\": %s,\n" +
                            "        \"deaths\": %s,\n" +
                            "        \"country\": \"France\",\n" +
                            "        \"population\": 64979548,\n" +
                            "        \"sq_km_area\": 551500,\n" +
                            "        \"life_expectancy\": \"78.8\",\n" +
                            "        \"elevation_in_meters\": 375,\n" +
                            "        \"continent\": \"Europe\",\n" +
                            "        \"abbreviation\": \"FR\",\n" +
                            "        \"location\": \"Western Europe\",\n" +
                            "        \"iso\": 250,\n" +
                            "        \"capital_city\": \"Paris\",\n" +
                            "        \"lat\": \"46.2276\",\n" +
                            "        \"long\": \"2.2137\",\n" +
                            "        \"updated\": \"2022-08-10 04:20:57\"\n" +
                            "    }\n" +
                            "}", EXPECTED_CONFIRMED, EXPECTED_RECOVERED, EXPECTED_DEATHS)
                    );
                }
                if (request.getPath().contains("/history")) {
                    return new MockResponse().setBody(String.format(
                            "{\n" +
                                    "    \"All\": {\n" +
                                    "        \"country\": \"France\",\n" +
                                    "        \"population\": 64979548,\n" +
                                    "        \"sq_km_area\": 551500,\n" +
                                    "        \"life_expectancy\": \"78.8\",\n" +
                                    "        \"elevation_in_meters\": 375,\n" +
                                    "        \"continent\": \"Europe\",\n" +
                                    "        \"abbreviation\": \"FR\",\n" +
                                    "        \"location\": \"Western Europe\",\n" +
                                    "        \"iso\": 250,\n" +
                                    "        \"capital_city\": \"Paris\",\n" +
                                    "        \"dates\": {\n" +
                                    "            \"%s\": %s,\n" +
                                    "            \"%s\": %s\n" +
                                    "        }\n" +
                                    "    }\n" +
                                    "}", getDay(1), EXPECTED_CONFIRMED, getDay(2), BEFORE_CONFIRMED)
                    );
                }
                if (request.getPath().contains("/vaccines")) {
                    return new MockResponse().setBody(String.format("{\n" +
                            "    \"All\": {\n" +
                            "        \"administered\": 150577493,\n" +
                            "        \"people_vaccinated\": %s,\n" +
                            "        \"people_partially_vaccinated\": 54529535,\n" +
                            "        \"country\": \"France\",\n" +
                            "        \"population\": %s,\n" +
                            "        \"sq_km_area\": 551500,\n" +
                            "        \"life_expectancy\": \"78.8\",\n" +
                            "        \"elevation_in_meters\": 375,\n" +
                            "        \"continent\": \"Europe\",\n" +
                            "        \"abbreviation\": \"FR\",\n" +
                            "        \"location\": \"Western Europe\",\n" +
                            "        \"iso\": 250,\n" +
                            "        \"capital_city\": \"Paris\",\n" +
                            "        \"updated\": \"2022/08/10 00:00:00+00\"\n" +
                            "    }\n" +
                            "}", VACCINATED, POPULATION)
                    );
                }
                return new MockResponse().setResponseCode(200);
            }
        };
        server.setDispatcher(mDispatcher);
        server.start();

        StatReceiver receiver = new CovidStatReceiver(server.url("/").toString());
        val stat = receiver.getStat(TEST_COUNTRY);
        assertEquals(EXPECTED_CONFIRMED, stat.getConfirmed());
        assertEquals(EXPECTED_RECOVERED, stat.getRecovered());
        assertEquals(EXPECTED_DEATHS, stat.getDeaths());
        assertEquals(VACCINATED / POPULATION * 100, stat.getVaccinated());
        assertEquals(EXPECTED_CONFIRMED - BEFORE_CONFIRMED, stat.getNewConfirmed());
        server.close();
    }

    @Test
    public void shouldThrowServerExceptionWhenWrongUrl() throws IOException {
        server.enqueue(new MockResponse());
        server.start();
        server.url("/");
        StatReceiver receiver = new CovidStatReceiver("http://some_url");
        ServerException thrown = assertThrows(ServerException.class, () -> receiver.getStat(TEST_COUNTRY));
        assertEquals("Exception during invoke by url: http://some_url/cases", thrown.getMessage());
        server.close();
    }

    @Test
    public void shouldThrowClientExceptionWhenParseFail() throws IOException {
        server.enqueue(new MockResponse().setBody("France"));
        server.start();
        StatReceiver receiver = new CovidStatReceiver(server.url("/").toString());
        ClientException thrown = assertThrows(ClientException.class, () -> receiver.getStat(TEST_COUNTRY));
        assertEquals("Fail to parse response body from /cases", thrown.getMessage());
        server.close();
    }

    @Test
    public void shouldThrowResourceNotFoundException() throws IOException {
        server.enqueue(new MockResponse().setBody("{}"));
        server.start();
        StatReceiver receiver = new CovidStatReceiver(server.url("/").toString());
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> receiver.getStat(TEST_COUNTRY));
        assertEquals("Resource: /cases not found for country: France", thrown.getMessage());
        server.close();
    }

    private String getDay(int minus) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        val yesterday = LocalDate.now().minusDays(minus);
        return yesterday.format(formatter);
    }
}
