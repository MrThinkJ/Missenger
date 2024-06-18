package com.mrthinkj.missenger;

import com.mrthinkj.missenger.service.StorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MissengerApplication implements CommandLineRunner {
    @Resource
    StorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(MissengerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        storageService.init();
    }

}
