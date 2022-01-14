package ca.nait.dmit.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AlbertaCovid19SummaryDataServiceTest {

    @Test
    void shouldContainNRecords() throws IOException {
        AlbertaCovid19SummaryDataService dataService = new AlbertaCovid19SummaryDataService();
        assertEquals(677, dataService.getDataList().size());

    }
}