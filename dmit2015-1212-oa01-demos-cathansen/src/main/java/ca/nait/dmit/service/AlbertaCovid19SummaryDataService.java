package ca.nait.dmit.service;

import ca.nait.dmit.domain.AlbertaCovid19SummaryData;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlbertaCovid19SummaryDataService {

    @Getter
    private List<AlbertaCovid19SummaryData> dataList = new ArrayList<>();

    public AlbertaCovid19SummaryDataService() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream( "/data/covid-19-alberta-statistics-summary-data.csv")))) {
            final var delimiter = ",";
            String line;
            // Skip the first line as it contains column headings
            reader.readLine();
            var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Read one line at a time from the input stream
            while ( (line = reader.readLine()) != null) {
                String[] values = line.split(delimiter, -1); //The -1 limit allows for any number of fields and not discard training empty fields.
                // Column order of fields:
                // 0 - "ID"
                // 1 - "date reported"
                // 2 - "number of lab tests"
                // 3 - "cumulative number of lab tests"
                // 4 - "number of cases"
                // 5 - "Cumulative number of cases"
                // 6 - "Active cases"
                // 7 - "Currently hospitalized"
                // 8 - "Currently in ICU"
                // 9 - "Cumulative number of deaths"
                // 10 - "Number of deaths"
                // 11 - "Number of variants of concern"
                // 12 - "Percent positivity"
                // Create an object from each row in the file
                AlbertaCovid19SummaryData lineData = new AlbertaCovid19SummaryData();
                lineData.setId(Integer.parseInt(values[0].replaceAll("\"","")));
                lineData.setDateReported(LocalDate.parse(values[1], dateFormatter));
                lineData.setNumberOfLabTests(Integer.parseInt(values[2]));
                lineData.setCumulativeNumberOfLabTests(Integer.parseInt(values[3]));
                lineData.setNumberOfCases(Integer.parseInt(values[4]));
                lineData.setCumulativeNumberOfCases(Integer.parseInt(values[5]));
                lineData.setActiveCases(Integer.parseInt(values[6]));
                lineData.setCurrentlyHospitalized(Integer.parseInt(values[7]));
                lineData.setCurrentlyInICU(Integer.parseInt(values[8]));
                lineData.setGetCumulativeNumberOfDeaths(Integer.parseInt(values[9]));
                lineData.setNumberOfDeaths(Integer.parseInt(values[10]));
                lineData.setNumberOfVariantsOfConcern(Integer.parseInt(values[11]));
                lineData.setPercentPositivity(Double.parseDouble(values[12]));

                // Add lineData to dataList
                dataList.add(lineData);
            }
        }
    }
}

