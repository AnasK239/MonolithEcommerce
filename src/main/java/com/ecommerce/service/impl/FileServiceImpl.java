package com.ecommerce.service.impl;

import com.ecommerce.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid image file name");
        }

        String uuid = UUID.randomUUID().toString();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = uuid.concat(extension);
        String filePath = path + File.separator + fileName;

        File folder = new File(path);
        if(!folder.exists()) folder.mkdir();

        Files.copy(image.getInputStream() , Paths.get(filePath));

        return fileName;
    }
}
