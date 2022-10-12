package com.example.imdb.service.cloudinary;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    CloudinaryImage upload(MultipartFile multipartFile);

    boolean delete(String publicId);
}
