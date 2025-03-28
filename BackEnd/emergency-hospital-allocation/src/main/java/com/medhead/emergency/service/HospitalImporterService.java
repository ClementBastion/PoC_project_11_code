package com.medhead.emergency.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.emergency.entity.Hospital;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.medhead.emergency.repository.HospitalRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HospitalImporterService {

    private final HospitalRepository hospitalRepository;

    // Automatic dependency injection
    @Autowired
    public HospitalImporterService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * Imports hospital data from the NHS directory API.
     * Fetches paginated data and saves/updates hospitals in the local repository.
     */
    public void importHospitalsFromNhs() {
        int offset = 0;
        int pageSize = 250;
        boolean hasMore = true;
        ObjectMapper mapper = new ObjectMapper();

        while (hasMore) {
            String url = "https://directory.spineservices.nhs.uk/ORD/2-0-0/organisations?PrimaryRoleId=RO197&Limit=" + pageSize;
            if (offset > 0) {
                url += "&Offset=" + offset;
            }

            try {
                // Establish connection and read JSON response
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestProperty("Accept", "application/json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String json = reader.lines().collect(Collectors.joining("\n"));

                JsonNode root = mapper.readTree(json);
                JsonNode organisations = root.get("Organisations");

                // Stop if there are no more organisations
                if (organisations == null || !organisations.isArray() || organisations.isEmpty()) {
                    hasMore = false;
                    break;
                }

                for (JsonNode org : organisations) {
                    String orgId = org.path("OrgId").asText();
                    Optional<Hospital> existingOpt = hospitalRepository.findById(orgId);
                    Hospital hospital = existingOpt.orElse(new Hospital());

                    // Map JSON fields to Hospital entity
                    hospital.setOrgId(orgId);
                    hospital.setName(org.path("Name").asText());
                    hospital.setStatus(org.path("Status").asText());
                    hospital.setPostcode(org.path("PostCode").asText());
                    hospital.setLastChangeDate(parseDate(org.path("LastChangeDate").asText()));
                    hospital.setPrimaryRoleId(org.path("PrimaryRoleId").asText());
                    hospital.setOrgLink(org.path("OrgLink").asText());

                    // Attempt to fetch additional location data from OrgLink endpoint
                    try {
                        String orgLinkUrl = hospital.getOrgLink();
                        if (orgLinkUrl != null && !orgLinkUrl.isBlank()) {
                            HttpURLConnection tmpConnection = (HttpURLConnection) new URL(orgLinkUrl).openConnection();
                            tmpConnection.setRequestProperty("Accept", "application/json");
                            BufferedReader tmpReader = new BufferedReader(new InputStreamReader(tmpConnection.getInputStream()));
                            String tmpJson = tmpReader.lines().collect(Collectors.joining("\n"));

                            JsonNode tmpRoot = mapper.readTree(tmpJson);
                            JsonNode tmpOrganisations = tmpRoot.get("Organisation");
                            JsonNode tmpGeoLoc = tmpOrganisations.get("GeoLoc");
                            JsonNode tmpLocation = tmpGeoLoc.get("Location");

                            // Extract and map address fields
                            hospital.setAddressLine1(tmpLocation.path("AddrLn1").asText());
                            hospital.setAddressLine2(tmpLocation.path("AddrLn2").asText());
                            hospital.setAddressLine3(tmpLocation.path("AddrLn3").asText());
                            hospital.setTown(tmpLocation.path("Town").asText());
                            hospital.setCountry(tmpLocation.path("Country").asText());
                            hospital.setUprn(tmpLocation.path("UPRN").asText());
                        }
                    } catch (Exception ex) {
                        // Non-blocking error log if location enrichment fails
                        System.err.println("❌ GeoLoc [" + orgId + "] : " + ex.getMessage());
                    }

                    // Log import or update operation
                    System.out.println((existingOpt.isPresent() ? "🔄 Update" : "✅ Import") + " : " + hospital.getName() + " [" + orgId + "]");
                    System.out.println("📌 Country : " + hospital.getCountry() + " | UPRN : " + hospital.getUprn());
                    System.out.println("----------------------------------------------------");

                    // Persist hospital to the repository
                    hospitalRepository.save(hospital);
                }

                offset += pageSize;

            } catch (Exception e) {
                e.printStackTrace();
                hasMore = false; // Exit loop on error
            }
        }
    }

    /**
     * Parses a date string in 'yyyy-MM-dd' format.
     * Returns null if parsing fails or input is blank.
     */
    private Date parseDate(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            return new SimpleDateFormat("yyyy-MM-dd").parse(value);
        } catch (Exception e) {
            return null;
        }
    }
}
