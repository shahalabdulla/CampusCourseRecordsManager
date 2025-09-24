package edu.ccrm.io;


import edu.ccrm.config.AppConfig;
import edu.ccrm.exceptions.DataExportException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupService {

    private final ImportExportService importExportService;
    private final AppConfig appConfig;

    public BackupService() {
        this.importExportService = new ImportExportService();
        this.appConfig = AppConfig.getInstance();
    }

    // Create a complete backup with timestamp
    public void createBackup() throws DataExportException {
        try {
            // Create timestamp for backup folder
            String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            Path backupDir = appConfig.getBackupFilePath().resolve("backup_" + timestamp);
            Files.createDirectories(backupDir);

            System.out.println("Creating backup in: " + backupDir.toAbsolutePath());

            // Export data to backup directory
            String studentsFile = backupDir.resolve("students.csv").toString();
            String coursesFile = backupDir.resolve("courses.csv").toString();

            importExportService.exportStudentsToCSV(studentsFile);
            importExportService.exportCoursesToCSV(coursesFile);

            System.out.println("✓ Backup completed successfully: " + backupDir.getFileName());

        } catch (IOException e) {
            throw new DataExportException("Backup failed: " + e.getMessage(), e);
        }
    }

    // Recursive method to calculate backup directory size
    public long calculateBackupSize() throws DataExportException {
        try {
            Path backupDir = appConfig.getBackupDirectory();

            if (!Files.exists(backupDir)) {
                return 0;
            }

            // Using Files.walk for recursive directory traversal
            final long[] totalSize = {0};

            Files.walk(backupDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            totalSize[0] += Files.size(file);
                        } catch (IOException e) {
                            System.out.println("Warning: Could not get size of " + file);
                        }
                    });

            return totalSize[0];

        } catch (IOException e) {
            throw new DataExportException("Failed to calculate backup size: " + e.getMessage(), e);
        }
    }

    // Recursive method to list backup files by depth
    public void listBackupFiles() throws DataExportException {
        try {
            Path backupDir = appConfig.getBackupDirectory();

            if (!Files.exists(backupDir)) {
                System.out.println("No backup directory found.");
                return;
            }

            System.out.println("Backup files structure:");
            System.out.println("=======================");

            // Recursive file listing with depth
            listFilesRecursive(backupDir, 0);

        } catch (IOException e) {
            throw new DataExportException("Failed to list backup files: " + e.getMessage(), e);
        }
    }

    // Recursive helper method for file listing
    private void listFilesRecursive(Path dir, int depth) throws IOException {
        if (!Files.isDirectory(dir)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                // Indentation based on depth
                String indent = "  ".repeat(depth);

                if (Files.isDirectory(entry)) {
                    System.out.println(indent + "[DIR] " + entry.getFileName());
                    listFilesRecursive(entry, depth + 1); // Recursive call
                } else {
                    long size = Files.size(entry);
                    System.out.println(indent + "[FILE] " + entry.getFileName() +
                            " (" + size + " bytes)");
                }
            }
        }
    }

    // Restore from latest backup
    public void restoreFromLatestBackup() throws DataExportException {
        try {
            Path backupDir = appConfig.getBackupDirectory();

            if (!Files.exists(backupDir)) {
                throw new DataExportException("No backup directory found");
            }

            // Find latest backup folder
            Path latestBackup = Files.list(backupDir)
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().startsWith("backup_"))
                    .max(Path::compareTo)
                    .orElseThrow(() -> new DataExportException("No backup found"));

            System.out.println("Restoring from: " + latestBackup.getFileName());

            String studentsFile = latestBackup.resolve("students.csv").toString();
            String coursesFile = latestBackup.resolve("courses.csv").toString();

            // Clear existing data first
            // Note: In a real application, you'd have methods to clear services

            // Import from backup
            importExportService.importStudentsFromCSV(studentsFile);
            importExportService.importCoursesFromCSV(coursesFile);

            System.out.println("✓ Restore completed successfully");

        } catch (IOException e) {
            throw new DataExportException("Restore failed: " + e.getMessage(), e);
        }
    }
}
