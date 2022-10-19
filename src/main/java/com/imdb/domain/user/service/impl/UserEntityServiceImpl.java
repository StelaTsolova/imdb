package com.imdb.domain.user.service.impl;

import com.imdb.domain.user.enums.Role;
import com.imdb.domain.user.model.dto.UserEntityRegisterDTO;
import com.imdb.domain.user.model.entity.User;
import com.imdb.domain.user.repository.UserEntityRepository;
import com.imdb.domain.user.model.dto.role.RoleChangeDTO;
import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.rating.model.entity.Rating;
import com.imdb.domain.user.mapping.UserEntityMapper;
import com.imdb.domain.user.service.UserEntityService;
import com.imdb.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserEntityRepository repository;
    private final UserEntityMapper userEntityMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(final UserEntityRegisterDTO userEntityRegisterDto) {
        final User userEntity = userEntityMapper.mapUserEntityRegisterDtoToUserEntity(userEntityRegisterDto);
        userEntity.setPassword(passwordEncoder.encode(userEntityRegisterDto.getPassword()));
        userEntity.setRole(Role.USER);

        repository.save(userEntity);
    }

    @Override
    public boolean doesNotExistByEmail(final String email) {
        return repository.findByEmail(email).isEmpty();
    }

    @Override
    public void changeUserRole(final RoleChangeDTO roleChangeDto) {
        final User user = getUserEntityByEmail(roleChangeDto.getUserEmail());
        user.setRole(Role.valueOf(roleChangeDto.getRole().toUpperCase()));

        repository.save(user);
    }

    @Override
    public User getUserEntityByEmail(final String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User with email " + email + " is not found."));
    }

    @Override
    public void removeRating(User userEntity, Rating rating) {
        userEntity.getRatings().remove(rating);

        repository.save(userEntity);
    }

    @Override
    public boolean hasRated(User userEntity, Movie movie) {
        Optional<Rating> rating = userEntity.getRatings().stream().filter(r -> r.getMovie() == movie).findFirst();

        return rating.isPresent();
    }
}
