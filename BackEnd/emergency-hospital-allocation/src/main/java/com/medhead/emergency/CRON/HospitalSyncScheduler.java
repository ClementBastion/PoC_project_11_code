package com.medhead.emergency.CRON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.medhead.emergency.service.HospitalImporterService;

/**
 * Scheduled task for automatic synchronization of hospital data
 * from the NHS directory.
 * <p>
 * This class is picked up by Spring's component scan and executes
 * the sync process daily at 01:00 AM.
 */
@Component
public class HospitalSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger(HospitalSyncScheduler.class);

    private final HospitalImporterService importer;

    // Constructor-based dependency injection of the importer service
    public HospitalSyncScheduler(HospitalImporterService importer) {
        this.importer = importer;
    }

    /**
     * Scheduled method to trigger hospital data synchronization.
     * <p>
     * The cron expression "0 0 1 * * *" means:
     * └── Every day at 01:00 AM (server time)
     * <p>
     * This uses Spring's @Scheduled annotation.
     */
    @Scheduled(cron = "0 0 1 * * *") // every day at 01:00 AM
    public void autoSyncHospitals() {
        logger.info("🕐 Starting daily hospital data synchronization...");
        importer.importHospitalsFromNhs();
        logger.info("✅ Hospital data synchronization completed.");
    }
}
