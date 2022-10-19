package com.imdb.domain.picture.service.impl;

import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.picture.model.entity.Picture;
import com.imdb.domain.picture.repository.PictureRepository;
import com.imdb.domain.picture.service.PictureService;
import com.imdb.cloudinary.CloudinaryImage;
import com.imdb.cloudinary.CloudinaryService;
import com.imdb.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
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
