package com.stat.requestor;

import com.stat.requestor.client.CovidStatReceiver;
import com.stat.requestor.client.StatReceiver;
import lombok.val;

import java.util.Scanner;

public class Main {
    private static final String BASE_URL = "https://covid-api.mmediagroup.fr/v1";

    public static void main(String[] args) {
        System.out.println("Enter country to display Covid-19 statistic");
        Scanner s = new Scanner(System.in);
        String country = s.nextLine();
        StatReceiver receiver = new CovidStatReceiver(BASE_URL);
        val franceRes = receiver.getStat(country);
        System.out.printf("Covid-19 statistic in %s: \n", country);
        System.out.println(franceRes.toString());
    }
}
