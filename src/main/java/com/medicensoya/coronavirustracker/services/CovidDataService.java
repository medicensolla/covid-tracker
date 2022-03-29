package com.medicensoya.coronavirustracker.services;

import com.medicensoya.coronavirustracker.models.LocationData;
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
public class CovidDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationData> locationsData = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL)).build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        this.parseCSV(httpResponse.body());

    }

    private void parseCSV(String bodyCSV) throws IOException {

        List<LocationData> newLocationDataStats = new ArrayList<>();
        StringReader csvBodyReader = new StringReader(bodyCSV);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
            LocationData locationData = new LocationData();
            locationData.setState(record.get("Province/State"));
            locationData.setCountry(record.get("Country/Region"));
            locationData.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
            locationData.setDifferenceFromPreviousDay(this.getCaseNumberDiffFromLastDay(locationData.getLatestTotalCases()
                    , record));
            newLocationDataStats.add(locationData);

        }
        this.locationsData = newLocationDataStats;

    }


    public int getTheSumOfTotalCases(List<LocationData> locationsData) {
        return locationsData.stream().mapToInt(data -> data.getLatestTotalCases()).sum();
    }

    public int getTotalNewCases(List<LocationData> locationsData) {
        return locationsData.stream().mapToInt(data -> data.getLatestTotalCases()).sum();
    }

    private int getCaseNumberDiffFromLastDay(int latestCases, CSVRecord record) {

        int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
        return latestCases - prevDayCases;

    }


    public List<LocationData> getLocationsData() {
        return locationsData;
    }
}
