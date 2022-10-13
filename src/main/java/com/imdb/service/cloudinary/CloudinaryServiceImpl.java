package com.imdb.service.cloudinary;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final String FOLDER_NAME = "imdb";
    private static final String TEMP_FILE = "temp-file";
    private static final String URL = "url";
    private static final String PUBLIC_ID = "public_id";

    private final Cloudinary cloudinary;


    @Override
    public CloudinaryImage upload(MultipartFile multipartFile) {
        File tempFile = null;

        try {
            tempFile = File.createTempFile(TEMP_FILE, multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);
            @SuppressWarnings("unchecked")
            Map<String, String> result = cloudinary.uploader().upload(tempFile, Map.of("folder", FOLDER_NAME));

            String url = result.getOrDefault(URL, "https://cdn2.vectorstock.com/i/1000x1000/82/41/404-error-page-not-found-funny-fat-cat-vector-21288241.jpg");
            String publicId = result.getOrDefault(PUBLIC_ID, "");

            return new CloudinaryImage(url, publicId);
        } catch (IOException exception) {
            System.out.println("upload -> " + exception.getMessage());
            return null;
        } finally {
            tempFile.delete();
        }
    }

    @Override
    public boolean delete(String publicId) {
        try {
            this.cloudinary.uploader().destroy(publicId, Map.of("folder", FOLDER_NAME));
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}