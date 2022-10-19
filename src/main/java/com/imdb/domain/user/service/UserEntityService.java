package com.imdb.domain.user.service;

import com.imdb.domain.user.model.dto.UserEntityRegisterDTO;
import com.imdb.domain.user.model.entity.User;
import com.imdb.domain.user.model.dto.role.RoleChangeDTO;
import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.rating.model.entity.Rating;

public interface UserEntityService {

    void registerUser(UserEntityRegisterDTO userEntityRegisterDto);

    boolean doesNotExistByEmail(String email);

    void changeUserRole(RoleChangeDTO roleChangeDto);

    User getUserEntityByEmail(String email);

    void removeRating(User userEntity, Rating rating);

    boolean hasRated(User userEntity, Movie movie);
}
