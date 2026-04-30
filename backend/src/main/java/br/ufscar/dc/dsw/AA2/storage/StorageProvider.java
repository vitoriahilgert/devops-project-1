package br.ufscar.dc.dsw.AA2.storage;

import br.ufscar.dc.dsw.AA2.exceptions.BadRequestException;
import br.ufscar.dc.dsw.AA2.exceptions.StorageException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
public class StorageProvider {
    private final String uploadDir = "uploads";
    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location");
        }
    }

    public Boolean exists(String path) {
        Path filePath = Paths.get(path);
        return Files.exists(filePath);
    }

    public String store(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String newFilename = UUID.randomUUID() + "-" + originalFilename;
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + originalFilename);
            }
            if (originalFilename.contains("..")) {
                throw new StorageException("Cannot store file with relative path outside current directory " + originalFilename);
            }

            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize();

            if (!destinationFile.getParent().equals(this.rootLocation)) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return Paths.get(uploadDir, newFilename).toString().replace("\\", "/");

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + originalFilename);
        }
    }

    public Resource load(String path) {
        Path filePath = Paths.get(path);
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new StorageException("Failed to read file: " + e.getMessage());
        }

        return resource;
    }

    public void delete(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + e.getMessage());
        }
    }

    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        boolean isValidFileName =
                !originalFilename.contains("..") &&
                        !originalFilename.contains("/") &&
                        !originalFilename.contains("\\") &&
                        !originalFilename.contains(":");

        if (!isValidFileName) {
            throw new StorageException("Filename contains invalid path sequence: " + originalFilename);
        }

        if (!Objects.equals(file.getContentType(), "image/png") &&
                !Objects.equals(file.getContentType(), "image/jpeg") &&
                !Objects.equals(file.getContentType(), "image/jpg")
        ) {
            throw new BadRequestException("Invalid file type: only PNG, JPG and JPEG files are allowed");
        }
    }
}
