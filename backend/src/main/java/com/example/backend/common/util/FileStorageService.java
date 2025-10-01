package com.example.backend.common.util;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final String uploadDir = "uploads"; // 파일을 저장할 디렉토리

    public FileStorageService() {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 파일 이름 정규화
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // 파일 이름에 유효하지 않은 문자가 있는지 확인
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            // 파일 확장자 추출
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            // UUID를 사용하여 고유한 파일 이름 생성
            String storedFileName = UUID.randomUUID().toString() + fileExtension;

            // 파일 저장 경로 확인
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 웹에서 접근 가능한 경로 반환 (예: /uploads/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.jpg)
            return "/" + uploadDir + "/" + storedFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }
}