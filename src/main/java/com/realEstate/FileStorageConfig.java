package com.realEstate.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Configuration
public class FileStorageConfig {

    @Value("${file.storage.directory}")
    private String storageDirectory;

}
