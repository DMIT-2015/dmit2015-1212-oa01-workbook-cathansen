package ca.nait.dmit.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlbertaCovid19CaseManagerTest {

    AlbertaCovid19CaseManager caseManager;
    @BeforeEach
    void beforeEach() throws IOException {
        //        AlbertaCovid19CaseManager caseManager = new AlbertaCovid19CaseManager();
        caseManager = AlbertaCovid19CaseManager.getInstance();
    }

    @Test
    void getAlbertaCovid19CaseList() throws IOException {
        assertEquals(436495, caseManager.getAlbertaCovid19CaseList().size());
    }

    @Test
    void activeCases() throws IOException {
        assertEquals(64_129, caseManager.countTotalActiveCases());
    }

    @Test
    void activeCasesByZone() throws IOException {
        assertEquals(29_544, caseManager.countActiveCasesByAhsZone("Calgary Zone"));
        assertEquals(24_062, caseManager.countActiveCasesByAhsZone("Edmonton Zone"));
    }

    @Test
    void distinctAhsZones(){
        List<String> ahsZoneList = caseManager.distinctAhsZones();
        ahsZoneList.forEach(System.out::println);
        assertEquals(6, ahsZoneList.size());
    }
}