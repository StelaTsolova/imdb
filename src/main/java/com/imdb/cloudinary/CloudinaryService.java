package com.imdb.cloudinary;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    CloudinaryImage upload(MultipartFile multipartFile);

    boolean delete(String publicId);
}
