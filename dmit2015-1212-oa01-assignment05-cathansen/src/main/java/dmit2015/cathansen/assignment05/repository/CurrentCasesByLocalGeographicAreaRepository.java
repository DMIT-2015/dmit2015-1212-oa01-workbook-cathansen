package dmit2015.cathansen.assignment05.repository;

import common.jpa.AbstractJpaRepository;
import dmit2015.cathansen.assignment05.entity.CurrentCasesByLocalGeographicArea;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Optional;

@ApplicationScoped
@Transactional
public class CurrentCasesByLocalGeographicAreaRepository extends AbstractJpaRepository<CurrentCasesByLocalGeographicArea, Long> {

    public CurrentCasesByLocalGeographicAreaRepository() {
        super(CurrentCasesByLocalGeographicArea.class);
    }

    public Optional<CurrentCasesByLocalGeographicArea> contains(double longitude, double latitude) {
        Optional<CurrentCasesByLocalGeographicArea> optionalSingleResult = Optional.empty();

        final String jpql = """
        SELECT a
        FROM CurrentCasesByLocalGeographicArea a
        WHERE contains(a.polygon, :pointValue) = true
        """;
        TypedQuery<CurrentCasesByLocalGeographicArea> query = getEntityManager().createQuery(jpql, CurrentCasesByLocalGeographicArea.class);

        Point geoLocation = new GeometryFactory()
                .createPoint(
                        new Coordinate( longitude, latitude  )
                );
        query.setParameter("pointValue", geoLocation);
        try {
            CurrentCasesByLocalGeographicArea singleResult = query.getSingleResult();
            optionalSingleResult = Optional.of(singleResult);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return optionalSingleResult;
    }

}