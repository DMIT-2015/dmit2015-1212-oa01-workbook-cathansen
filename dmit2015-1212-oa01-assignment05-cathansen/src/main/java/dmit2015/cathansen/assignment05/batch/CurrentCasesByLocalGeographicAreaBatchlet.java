package dmit2015.cathansen.assignment05.batch;

import dmit2015.cathansen.assignment05.entity.CurrentCasesByLocalGeographicArea;
import dmit2015.cathansen.assignment05.repository.CurrentCasesByLocalGeographicAreaRepository;
import jakarta.batch.api.AbstractBatchlet;
import jakarta.batch.api.BatchProperty;
import jakarta.batch.runtime.context.JobContext;
import jakarta.batch.runtime.BatchStatus;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Batchlets are task oriented step that is called once.
 * It either succeeds or fails. If it fails, it CAN be restarted and it runs again.
 */
@Named
public class CurrentCasesByLocalGeographicAreaBatchlet extends AbstractBatchlet {

    @Inject
    private CurrentCasesByLocalGeographicAreaRepository _repository;

    @Inject
    private JobContext _jobContext;

    private Logger _logger = Logger.getLogger(CurrentCasesByLocalGeographicAreaBatchlet.class.getName());

    @Inject
    @BatchProperty(name = "input_file")
    private String inputFile;

    /**
     * Perform a task and return "COMPLETED" if the job has successfully completed
     * otherwise return "FAILED" to indicate the job failed to complete.
     */
    @Transactional
    @Override
    public String process() throws Exception {
        String batchStatus = BatchStatus.COMPLETED.toString();

        _repository.deleteAll();

        Properties jobParameters = _jobContext.getProperties();
//        String inputFile = jobParameters.getProperty("input_file");

        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(inputFile).toFile()))){
            String line;
            final String delimiter = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
            // We can skip the first line as it contains column headings
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] tokens = line.split(delimiter, -1);

                    CurrentCasesByLocalGeographicArea currentCurrentCasesByLocalGeographicArea = new CurrentCasesByLocalGeographicArea();
                    //need to parse the date properly format is 2022/03/17
                    String dateText = tokens[0].replaceAll("/", "-");
                    currentCurrentCasesByLocalGeographicArea.setDate(LocalDate.parse(dateText));
                    currentCurrentCasesByLocalGeographicArea.setLocation(tokens[1]);
                    currentCurrentCasesByLocalGeographicArea.setPopulation(Integer.valueOf(tokens[2]));
                    currentCurrentCasesByLocalGeographicArea.setTotalCases(Integer.valueOf(tokens[4]));
                    currentCurrentCasesByLocalGeographicArea.setActiveCases(Integer.valueOf(tokens[5]));
                    currentCurrentCasesByLocalGeographicArea.setRecoveredCases(Integer.valueOf(tokens[7]));
                    currentCurrentCasesByLocalGeographicArea.setDeaths(Integer.valueOf(tokens[8]));
                    currentCurrentCasesByLocalGeographicArea.setOneDose(Integer.valueOf(tokens[9]));
                    currentCurrentCasesByLocalGeographicArea.setFullyImmunized(Integer.valueOf(tokens[10]));
                    currentCurrentCasesByLocalGeographicArea.setTotalDoses(Integer.valueOf(tokens[11]));
                    currentCurrentCasesByLocalGeographicArea.setOneDosePercentage(Double.valueOf(tokens[12]));
                    currentCurrentCasesByLocalGeographicArea.setFullyImmunizedPercentage(Double.valueOf(tokens[13]));
                    //polygon:
                    String wktText = tokens[14].replaceAll("\"", "");
                    MultiPolygon multiPolygon = (MultiPolygon) new WKTReader().read(wktText);
                    currentCurrentCasesByLocalGeographicArea.setPolygon(multiPolygon);

                    _repository.create(currentCurrentCasesByLocalGeographicArea);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return batchStatus;
    }
}