package com.nirzhor.coronatrack.Coronavirustracker.services;

import com.nirzhor.coronatrack.Coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String VIRUS_DATA_DEATH = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";

    private List<LocationStats> allStatsConfirmed = new ArrayList<>();
    private List<LocationStats> allStatsDeath = new ArrayList<>();

    public List<LocationStats> getAllStatsConfirmed() {
        return allStatsConfirmed;
    }

    public List<LocationStats> getAllStatsDeath() {
        return allStatsDeath;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException,InterruptedException {

        List<LocationStats> newStatsConfirmed = new ArrayList<>();
        List<LocationStats> newStatsDeath = new ArrayList<>();

        HttpClient clientConfirmed = HttpClient.newHttpClient();
        HttpRequest requestConfirmed = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        HttpClient clientDeath = HttpClient.newHttpClient();
        HttpRequest requestDeath = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_DEATH))
                .build();

        HttpResponse<String> httpResponseConfirmed =  clientConfirmed.send(requestConfirmed, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> httpResponseDeath =  clientDeath.send(requestDeath, HttpResponse.BodyHandlers.ofString());

        // System.out.println(httpResponse.body());

        StringReader csvBodyReaderConfirmed = new StringReader(httpResponseConfirmed.body());
        Iterable<CSVRecord> recordsConfirmed = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderConfirmed);

        StringReader csvBodyReaderDeath = new StringReader(httpResponseDeath.body());
        Iterable<CSVRecord> recordsDeath = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderDeath);

        for (CSVRecord recordConfirmed : recordsConfirmed) {
            LocationStats locationStat = new LocationStats();

            locationStat.setState(recordConfirmed.get("Province/State"));
            locationStat.setCountry(recordConfirmed.get("Country/Region"));

            int latestCasesConfirmed = Integer.parseInt(recordConfirmed.get(recordConfirmed.size() - 1));
            int prevDayCasesConfirmed = Integer.parseInt(recordConfirmed.get(recordConfirmed.size() - 2));

            locationStat.setLatestTotalCases(latestCasesConfirmed);
            locationStat.setDiffFromPrevDay(latestCasesConfirmed - prevDayCasesConfirmed);
          //  System.out.println(locationStat);
          //  System.out.println(state);
            newStatsConfirmed.add(locationStat);
        }

       

        this.allStatsConfirmed = newStatsConfirmed;
        this.allStatsDeath = newStatsDeath;
    }
}


