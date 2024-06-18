package com.mrthinkj.missenger.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();
    String save(MultipartFile file);
    Resource get(String filename);
    void delete(String filename);
}
