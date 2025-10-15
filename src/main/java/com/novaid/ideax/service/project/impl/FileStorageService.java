package com.novaid.ideax.service.project.impl;


import com.novaid.ideax.entity.auth.Account;
// Bạn cần tạo Repository này
import com.novaid.ideax.entity.project.FileStorage;
import com.novaid.ideax.repository.project.FileStorageRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service

public class FileStorageService {

    private final Path fileStorageLocation;
    private final FileStorageRepository fileStorageRepository;

    public FileStorageService(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory.", ex);
        }
    }

    public FileStorage storeFile(MultipartFile file, String subDir, Account uploader) {
        if (file == null || file.isEmpty()) return null;

        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension;
        String relativePath = "/" + subDir + "/" + newFileName;

        try {
            Path targetDir = this.fileStorageLocation.resolve(subDir);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(newFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            FileStorage fileEntity = FileStorage.builder()
                    .fileName(newFileName)
                    .originalFileName(originalFileName)
                    .filePath(relativePath)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploader(uploader)
                    .build();
            return fileStorageRepository.save(fileEntity);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file.", ex);
        }
    }

    public void deletePhysicalFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            Path path = this.fileStorageLocation.resolve(filePath.substring(1)).normalize();
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filePath);
        }
    }
}