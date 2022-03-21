package dmit2015.cathansen.assignment05.ejb;

import common.ejb.EmailSessionBean;
import dmit2015.cathansen.assignment05.entity.CurrentCasesByLocalGeographicArea;
import dmit2015.cathansen.assignment05.repository.CurrentCasesByLocalGeographicAreaRepository;
import jakarta.annotation.Resource;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.ws.rs.POST;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
public class CurrentCasesByLocalGeographicAreaTimersSessionBean {
    private Logger _logger = Logger.getLogger(CurrentCasesByLocalGeographicAreaTimersSessionBean.class.getName());

    @Resource
    private TimerService timerService;

    @Inject
    @ConfigProperty(name="dmit2015.cathansen.DownloadUri")
    private String DOWNLOAD_URI;

    @Inject
    @ConfigProperty(name="dmit2015.cathansen.DownloadFolder")
    private String DOWNLOAD_DIRECTORY;

    @Inject
    @ConfigProperty(name="dmit2015.cathansen.MailToAddresses")
    private String mailToAddress;

    @Inject
    @ConfigProperty(name = "dmit2015.cathansen.BatchJobXmlFileName")
    String jobXmlFile;

    @Inject
    private EmailSessionBean mail;

    @Inject
    CurrentCasesByLocalGeographicAreaRepository currentCasesByLocalGeographicAreaRepository;

    //download the latest data every weekday at 4pm
    @Schedules({@Schedule(second = "0", minute ="29", hour = "3", dayOfWeek = "Mon,Tue,Wed,Thu,Fri", month = "Jan-Apr", year = "2022", info ="Download Updated Alberta Covid Information", persistent = false)})
    private void downloadFile(Timer timer) {
        //download the file
        HttpClient client = HttpClient.newHttpClient();
        // HashMap<String, String> info = (HashMap<String, String>) timer.getInfo();
        String downloadUriString = DOWNLOAD_URI;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadUriString))
                .build();
        String downloadDirectory = DOWNLOAD_DIRECTORY;
        Path downloadPath = Path.of(downloadDirectory);
        try {
            HttpResponse<Path> response = client.send(request,
                    HttpResponse.BodyHandlers.ofFileDownload(downloadPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            _logger.info("Finished download file to " + response.body());
        } catch (Exception e) {
            _logger.fine("Error downloading file. " + e.getMessage());
            e.printStackTrace();
        }

        // start batch job to import file into database
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        long jobId = jobOperator.start(jobXmlFile, null);

        // Create an interval timer that starts in 3 seconds and repeats every 5 seconds and passed in the jobId to the timer
        timerService.createTimer(3000, 5000, jobId);



    }

    private void sendEmail(Timer timer, String subject, String body) {

        if (!mailToAddress.isBlank()) {
            long jobId = (long) timer.getInfo();
            String mailSubject = subject;
            String mailText = body;
            try {
                mail.sendTextEmail(mailToAddress, mailSubject, mailText);
                _logger.info("Successfully sent email to " + mailToAddress);
            } catch (Exception e) {
                e.printStackTrace();
                _logger.fine("Error sending email with exception " + e.getMessage());
            }
        }
    }


    @Timeout
    public void checkBatchJobStatus(Timer timer) {
        // Extract the jobId from the timer
        long jobId = (long) timer.getInfo();
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        JobExecution jobExecution = jobOperator.getJobExecution(jobId);
        if (jobExecution.getBatchStatus() == BatchStatus.COMPLETED) {
            //TODO: fix this
            //List<CurrentCasesByLocalGeographicArea> entities = CurrentCasesByLocalGeographicAreaRepository.list();
            sendEmail(timer, "DMIT2015 Assignment5 Batch Job COMPLETED", "List<CurrentCasesByLocalGeographicArea>");
            timer.cancel();
            // send email to notify batch job has completed
            //

        } else if (jobExecution.getBatchStatus() == BatchStatus.FAILED) {
            // send email to notify batch job has failed
            String emailBody = "The following Job ID failed: " + jobId;
            sendEmail(timer, "DMIT2015 Assignment5 Batch Job FAILED", emailBody);
            timer.cancel();
        }

    }
}
