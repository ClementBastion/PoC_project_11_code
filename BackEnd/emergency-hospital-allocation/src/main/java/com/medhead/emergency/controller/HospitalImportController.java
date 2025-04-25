package com.medhead.emergency.controller;

import org.springframework.web.bind.annotation.*;
import com.medhead.emergency.service.HospitalImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller to manually trigger hospital data import.
 * <p>
 * This endpoint is useful for testing or force-refreshing data
 * without waiting for the scheduled task.
 */
@RestController
@RequestMapping("/api/import")
public class HospitalImportController {

    private static final Logger logger = LoggerFactory.getLogger(HospitalImportController.class);

    private final HospitalImporterService importer;

    // Constructor-based injection of the hospital importer service
    public HospitalImportController(HospitalImporterService importer) {
        this.importer = importer;
    }

    /**
     * HTTP GET endpoint to trigger a manual import of hospital data
     * from the NHS directory.
     * <p>
     * URL: /api/import/hospitals
     *
     * @return success message once the import is complete
     */
    @GetMapping("/hospitals")
    public String triggerImport() {
        logger.info("🚀 Manual hospital data import triggered via API.");
        importer.importHospitalsFromNhs();
        return "✅ Hospital import completed successfully.";
    }
}
