package edu.ccrm.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig {
    private static AppConfig instance;
    private Path dataDirectory;
    private Path backupDirectory;

    private AppConfig() {
        // Private constructor to prevent instantiation
        initializeDirectories();
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void initializeDirectories() {
        try {
            // Create data directory in user home
            String userHome = System.getProperty("user.home");
            dataDirectory = Paths.get(userHome, "ccrm_data");
            backupDirectory = dataDirectory.resolve("backups");

            // Create directories if they don't exist
            java.nio.file.Files.createDirectories(dataDirectory);
            java.nio.file.Files.createDirectories(backupDirectory);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application directories", e);
        }
    }

    public Path getDataDirectory() { return dataDirectory; }
    public Path getBackupDirectory() { return backupDirectory; }

    public Path getExportFilePath(String filename) {
        return dataDirectory.resolve(filename);
    }

    public Path getBackupFilePath() {
        String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return backupDirectory.resolve("backup_" + timestamp);
    }
}