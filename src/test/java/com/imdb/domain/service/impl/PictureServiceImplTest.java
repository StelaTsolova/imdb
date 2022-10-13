package com.imdb.domain.service.impl;

import com.imdb.domain.model.entity.Picture;
import com.imdb.domain.repository.PictureRepository;
import com.imdb.domain.service.PictureService;
import com.imdb.domain.service.cloudinary.CloudinaryImage;
import com.imdb.domain.service.cloudinary.CloudinaryService;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import com.imdb.domain.service.impl.PictureServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PictureServiceImplTest {
    public static final String PICTURE_URL = "http://localhost:8080";
    public static final String PICTURE_PUBLIC_ID = "123";

    private PictureService pictureServiceTest;
    private Picture pictureTest;

    @Mock
    private PictureRepository pictureRepositoryMock;
    @Mock
    private CloudinaryService cloudinaryServiceMock;

    @BeforeEach
    void init(){
        pictureServiceTest = new PictureServiceImpl(pictureRepositoryMock, cloudinaryServiceMock);

        pictureTest = new Picture();
        pictureTest.setUrl(PICTURE_URL);
        pictureTest.setPublicId(PICTURE_PUBLIC_ID);
    }

    @Test
    public void savePicture(){
        Mockito.when(cloudinaryServiceMock.upload(Mockito.any())).thenReturn(new CloudinaryImage(PICTURE_URL, PICTURE_PUBLIC_ID));
        Mockito.when(pictureRepositoryMock.save(Mockito.any())).thenReturn(pictureTest);

        final Picture picture = pictureServiceTest.savePicture(null, null);

        Assertions.assertEquals(picture.getUrl(), pictureTest.getUrl());
        Assertions.assertEquals(picture.getPublicId(), pictureTest.getPublicId());
    }

    @Test
    public void deletePictureByUrl(){
        Mockito.when(pictureRepositoryMock.findByUrl(PICTURE_URL)).thenReturn(Optional.of(pictureTest));
        Mockito.when(cloudinaryServiceMock.delete(PICTURE_PUBLIC_ID)).thenReturn(true);

        pictureServiceTest.deletePictureByUrl(PICTURE_URL);

        Mockito.verify(pictureRepositoryMock, times(1)).delete(pictureTest);
    }

    @Test
    public void deletePictureByUrlShouldThrowWhenPictureWithUrlNotExist(){
        Mockito.when(pictureRepositoryMock.findByUrl(PICTURE_URL)).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> pictureServiceTest.deletePictureByUrl(PICTURE_URL));
    }
}