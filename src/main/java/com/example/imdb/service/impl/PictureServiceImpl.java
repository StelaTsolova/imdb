package com.example.imdb.service.impl;

import com.example.imdb.model.entity.Movie;
import com.example.imdb.model.entity.Picture;
import com.example.imdb.repository.PictureRepository;
import com.example.imdb.service.cloudinary.CloudinaryImage;
import com.example.imdb.service.cloudinary.CloudinaryService;
import com.example.imdb.service.PictureService;
import com.example.imdb.web.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Picture savePicture(MultipartFile image, Movie movie) {
        CloudinaryImage cloudinaryImage = cloudinaryService.upload(image);

        Picture picture = new Picture(cloudinaryImage.getUrl(), cloudinaryImage.getPublicId());
        picture.setMovie(movie);

        log.info("Created new picture");
        return pictureRepository.save(picture);
    }

    @Override
    public void deletePictureByUrl(String url) {
        Picture picture = pictureRepository.findByUrl(url)
                .orElseThrow(() -> new ObjectNotFoundException("Picture with url " + url + " is not found!"));

        if (this.cloudinaryService.delete(picture.getPublicId())) {
            log.info("Deleted picture with id {}", picture.getId());
            pictureRepository.delete(picture);
        }
    }
}
