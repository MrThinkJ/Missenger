package com.mrthinkj.missenger.service.impl;

import com.mrthinkj.missenger.service.StorageService;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final Path root = Paths.get("uploads");
    @Override
    public void init() {
        if (!Files.exists(root)){
            try {
                Files.createDirectories(root);
            } catch (Exception e){
                throw new RuntimeException("Could not initialize folder for upload");
            }
        }
    }

    @Override
    public String save(MultipartFile file) {
        String fileName = UUID.randomUUID().toString();
        String filePath = fileName+"."+ FilenameUtils.getExtension(file.getOriginalFilename());
        try{
            Files.copy(file.getInputStream(), root.resolve(filePath));
        } catch (IOException e){
            throw new RuntimeException("Could not save this file");
        }
        return fileName;
    }

    @Override
    public Resource get(String filename) {
        try{
            Path filePath = root.resolve(filename);
            Resource fileResource = new UrlResource(filePath.toUri());
            if (fileResource.exists() && fileResource.isReadable()){
                return fileResource;
            } else {
                throw new RuntimeException("Could not read this file");
            }
        } catch (MalformedURLException e){
            throw new RuntimeException("Could not load this file");
        }
    }

    @Override
    public void delete(String filename) {
        try{
            Path filePath = root.resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e){
            throw new RuntimeException("Could not delete this file");
        }
    }
}
