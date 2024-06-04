package com.example.vocabulary.service.fss;

import com.example.vocabulary.exception.FileUploadException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class LocalFileStorageService implements FileStorageService{

    @Override
    public String save(MultipartFile photo, String id, String storagePath) {
        var extension = getExtension(photo);
        try {
            var storageFolder = ResourceUtils.getFile("classpath:%s".formatted(storagePath));
            var file = Path.of(storageFolder.getAbsolutePath(), id + "." + extension);
            Files.copy(photo.getInputStream(), file, StandardCopyOption.REPLACE_EXISTING);
            var result = file.toString().split("\\\\");
            return "/" + result[result.length - 2] + "/" + result[result.length - 1];

        } catch (IOException e) {
            throw new FileUploadException(e.getMessage());
        }
    }
}
