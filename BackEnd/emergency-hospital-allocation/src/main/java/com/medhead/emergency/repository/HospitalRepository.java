package com.medhead.emergency.repository;

import com.medhead.emergency.entity.Hospital;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for accessing and managing Hospital entities.
 * <p>
 * Extends JpaRepository to inherit standard CRUD operations and query methods.
 * <p>
 * - Hospital: the entity type managed by this repository.
 * - String: the type of the entity's primary key (Hospital's ID).
 * <p>
 * Spring Data JPA will automatically provide the implementation at runtime.
 */
public interface HospitalRepository extends JpaRepository<Hospital, String> {
    @Cacheable("allHospitals")
    List<Hospital> findAll();

    @Query(value = """
    SELECT h.org_id
    FROM hospital h
    ORDER BY
      h.geom <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
    LIMIT :limit
    """,
            nativeQuery = true)
    List<String> findNearest(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("limit") int limit
    );

    /**
     * From a list of hospital IDs, return only those that have
     * at least one available bed for the given speciality ID.
     */
    @Query("""
        SELECT DISTINCT h
        FROM Hospital h
        JOIN h.hospitalSpecialities hs
        WHERE h.orgId IN :ids
          AND hs.speciality.id       = :specId
          AND hs.availableBeds      >  0
        """)
    List<Hospital> findAvailableByIdsAndSpecialityId(
            @Param("ids")   List<String> ids,
            @Param("specId") Integer specialityId
    );

    @Modifying
    @Query("""
    UPDATE HospitalSpeciality hs
    SET hs.availableBeds = hs.availableBeds - 1
    WHERE hs.hospital.orgId = :hid
      AND hs.speciality.id = :sid
      AND hs.availableBeds > 0
  """)
    int decrementAvailableBeds(
            @Param("hid") String  hospitalId,
            @Param("sid") Integer specialityId
    );


    @Query(
            value = """
        SELECT h.*
        FROM hospital h
        JOIN hospital_speciality hs
          ON hs.hospital_org_id = h.org_id
         AND hs.speciality_id   = :specId
         AND hs.available_beds  >  0
        ORDER BY h.geom <-> ST_SetSRID(ST_MakePoint(:lon, :lat),4326)::geography
        LIMIT :limit
      """,
            nativeQuery = true
    )
    List<Hospital> findNearestAvailable(
            @Param("lat")      double lat,
            @Param("lon")      double lon,
            @Param("specId")   int specId,
            @Param("limit")    int limit
    );
}