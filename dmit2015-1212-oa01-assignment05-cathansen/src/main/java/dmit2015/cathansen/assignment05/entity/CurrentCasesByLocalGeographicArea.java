package dmit2015.cathansen.assignment05.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.MultiPolygon;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "chansen31CurrentCasesByLocalGeographicArea")
public class CurrentCasesByLocalGeographicArea implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate date;

    @Column
    private String location;

    @Column
    private Integer population;

    @Column
    private Integer totalCases;

    @Column
    private Integer activeCases;

    @Column
    private Integer recoveredCases;

    @Column
    private Integer deaths;

    @Column
    private Integer oneDose;

    @Column
    private Integer fullyImmunized;

    @Column
    private Integer totalDoses;

    @Column
    private Double oneDosePercentage;

    @Column
    private Double fullyImmunizedPercentage;

    @JsonbTransient
    @Column(name = "polygon")
    private MultiPolygon polygon;

    @JsonbTransient
    private LocalDateTime createdDateTime;

//    @Column(nullable = false)
//    private LocalDateTime lastModifiedDateTime;

    @PrePersist
    private void beforePersist() {
        createdDateTime = LocalDateTime.now();
        //lastModifiedDateTime = createdDateTime;
    }


}
