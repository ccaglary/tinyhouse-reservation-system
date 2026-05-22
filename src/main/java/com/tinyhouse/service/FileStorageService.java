package com.tinyhouse.service;

import com.tinyhouse.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String subdirectory) {
        try {
            if (file.isEmpty()) {
                throw new BusinessException("Boş dosya yüklenemez");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = UUID.randomUUID().toString() + extension;
            Path targetDir = Paths.get(uploadDir, subdirectory);
            Files.createDirectories(targetDir);

            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = "/uploads/" + subdirectory + "/" + filename;
            log.info("File stored: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new BusinessException("Dosya yüklenirken bir hata oluştu");
        }
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith("/uploads/")) {
                Path path = Paths.get(uploadDir, filePath.substring("/uploads/".length()));
                Files.deleteIfExists(path);
                log.info("File deleted: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", filePath, e);
        }
    }
}
