package com.imdb.service;

import com.imdb.model.entity.Movie;
import com.imdb.model.entity.Picture;
import org.springframework.web.multipart.MultipartFile;


public interface PictureService {

    Picture savePicture(MultipartFile image, Movie movie);

    void deletePictureByUrl(String url);
}
