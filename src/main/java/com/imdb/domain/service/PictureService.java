package com.imdb.domain.service;

import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.entity.Picture;
import org.springframework.web.multipart.MultipartFile;


public interface PictureService {

    Picture savePicture(MultipartFile image, Movie movie);

    void deletePictureByUrl(String url);
}
