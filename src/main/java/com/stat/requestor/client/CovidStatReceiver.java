package com.stat.requestor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stat.requestor.dto.CaseResponse;
import com.stat.requestor.dto.HistoryResponse;
import com.stat.requestor.dto.VaccinesResponse;
import com.stat.requestor.exception.ClientException;
import com.stat.requestor.exception.ResourceNotFoundException;
import com.stat.requestor.exception.ServerException;
import com.stat.requestor.model.StatResult;
import com.stat.requestor.model.Status;
import lombok.val;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class CovidStatReceiver implements StatReceiver {

    private static final String CASES_URL = "/cases";
    private static final String VACCINES_URL = "/vaccines";
    private static final String HISTORY_URL = "/history";
    private static final String COUNTRY_KEY = "country";
    private static final String STATUS_KEY = "status";

    private final Logger logger = Logger.getAnonymousLogger();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;

    public CovidStatReceiver(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public StatResult getStat(String country) {
        logger.log(Level.INFO, "start receiving");
        val params = Map.of(COUNTRY_KEY, country);

        val cases = getResponse(CASES_URL, params, CaseResponse.class).getCases();
        if (cases == null) {
            throw new ResourceNotFoundException(CASES_URL, country);
        }
        val statResult = cases.toResult();

        val vaccines = getResponse(VACCINES_URL, params, VaccinesResponse.class).getVaccines();
        if (vaccines == null) {
            throw new ResourceNotFoundException(VACCINES_URL, country);
        }
        statResult.setVaccinated(vaccines.vaccinatedPercentage());

        val historyParams = Map.of(COUNTRY_KEY, country, STATUS_KEY, Status.CONFIRMED.getValue());
        val history = getResponse(HISTORY_URL, historyParams, HistoryResponse.class).getHistory();
        if (history == null) {
            throw new ResourceNotFoundException(HISTORY_URL, country);
        }
        statResult.setNewConfirmed(history.findNewVaccinated(statResult.getConfirmed()));
        logger.log(Level.INFO, "receiving successful");
        return statResult;
    }

    private <T> T getResponse(String url, Map<String, String> params, Class<T> toClass) {
        val casesResponseBody = request(baseUrl + url, params);
        T response;
        try {
            response = objectMapper.readValue(casesResponseBody.string(), toClass);
        } catch (IOException e) {
            throw new ClientException(format("Fail to parse response body from %s", url), e);
        }
        if (response == null) {
            throw new ResourceNotFoundException(url, params.get(COUNTRY_KEY));
        }
        return response;
    }

    public ResponseBody request(String url, Map<String, String> params) {
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        Request request = new Request.Builder().url(httpBuilder.build()).build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new ServerException(format("Exception during invoke by url: %s", url), e);
        }
       return response.body();
    }
}
