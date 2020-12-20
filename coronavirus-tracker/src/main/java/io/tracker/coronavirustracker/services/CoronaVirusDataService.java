package io.tracker.coronavirustracker.services;

import io.tracker.coronavirustracker.models.LocationStats;
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
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    //URL String with necessary data
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/" +
            "master/csse_covid_19_data/csse_covid_19_time_series/" +
            "time_series_covid19_confirmed_global.csv";

    @PostConstruct
 /*   cron expressions: <minute> <hour> <day-of-month> <month> <day-of-week> <command>*/
    /*execute the method every day*/
    @Scheduled(cron = "* * 1 * * *")
    public void receiveVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        //creating new client
        HttpClient client = HttpClient.newHttpClient();
        //creating new request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        //sending request to client
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());


        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();

            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));

            locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));

            newStats.add(locationStat);
        }
        this.allStats=newStats;

    }
}
