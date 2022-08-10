package com.stat.requestor.client;

import com.stat.requestor.model.StatResult;
import com.stat.requestor.exception.ResourceNotFoundException;
import com.stat.requestor.exception.ClientException;
import com.stat.requestor.exception.ServerException;

public interface StatReceiver {

    /**
     * Receive and get Covid-19 statistics by country
     *
     * @param country for which statistics are needed
     * @return Covid-19 statistics
     * @throws ResourceNotFoundException when statistics not found for country
     * @throws ClientException when some error on client side
     * @throws ServerException when some error on server side
     * */
    StatResult getStat(String country);
}
