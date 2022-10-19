package com.imdb.domain.picture.service;

import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.picture.model.entity.Picture;
import org.springframework.web.multipart.MultipartFile;


public interface PictureService {

    Picture savePicture(MultipartFile image, Movie movie);

    void deletePictureByUrl(String url);
}
