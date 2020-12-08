package io.harmed.coronavirustracker.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service //the annotation
public class CoronaVirusDataService {
    //url of the database of current cases
    private  static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/" +
            "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    @PostConstruct
    public void fetchVirusData() throws IOException, InterruptedException {
        //Returns a new HttpClient with default settings
        HttpClient client = HttpClient.newHttpClient();

        //Creates a new HttpClient builder.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        //response to client(which is String type; catch IOException because of send() method in the method signature)
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        //creating StringReader for parsing
        StringReader cvsBpdyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(cvsBpdyReader);
        for (CSVRecord record : records) {
            String state = record.get("Province/State");
            //print the first column of the database 
            System.out.println(state);

            String customerNo = record.get("CustomerNo");
            String name = record.get("Name");
        }
    }
}
