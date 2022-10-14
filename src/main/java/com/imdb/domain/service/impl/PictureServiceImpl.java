package com.imdb.domain.service.impl;

import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.entity.Picture;
import com.imdb.domain.repository.PictureRepository;
import com.imdb.domain.service.PictureService;
import com.imdb.domain.service.cloudinary.CloudinaryImage;
import com.imdb.domain.service.cloudinary.CloudinaryService;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    private final PictureRepository pictureRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Picture savePicture(final MultipartFile image, final Movie movie) {
        final CloudinaryImage cloudinaryImage = cloudinaryService.upload(image);

        final Picture picture = new Picture(cloudinaryImage.getUrl(), cloudinaryImage.getPublicId());
        picture.setMovie(movie);

        return pictureRepository.save(picture);
    }

    @Override
    public void deletePictureByUrl(final String url) {
        final Picture picture = pictureRepository.findByUrl(url)
                .orElseThrow(() -> new ObjectNotFoundException("Picture with url " + url + " is not found!"));

        if (cloudinaryService.delete(picture.getPublicId())) {
            pictureRepository.delete(picture);
        }
    }
}
