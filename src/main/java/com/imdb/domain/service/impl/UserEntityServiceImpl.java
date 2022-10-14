package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.RoleChangeDto;
import com.imdb.domain.model.dto.UserEntityRegisterDto;
import com.imdb.domain.model.entity.UserEntity;
import com.imdb.domain.model.enums.Role;
import com.imdb.domain.model.mapping.UserEntityMapper;
import com.imdb.domain.repository.UserEntityRepository;
import com.imdb.domain.service.UserEntityService;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserEntityRepository repository;
    private final UserEntityMapper userEntityMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(final UserEntityRegisterDto userEntityRegisterDto) {
        final UserEntity userEntity = userEntityMapper.mapUserEntityRegisterDtoToUserEntity(userEntityRegisterDto);
        userEntity.setPassword(passwordEncoder.encode(userEntityRegisterDto.getPassword()));
        userEntity.setRole(Role.USER);

        repository.save(userEntity);
    }

    @Override
    public boolean isNotExistByEmail(final String email) {
        return repository.findByEmail(email).isEmpty();
    }

    @Override
    public void changeUserRole(final RoleChangeDto roleChangeDto) {
        final UserEntity user = getUserEntityByEmail(roleChangeDto.getUserEmail());
        user.setRole(Role.valueOf(roleChangeDto.getRole().toUpperCase()));

        repository.save(user);
    }

    @Override
    public UserEntity getUserEntityByEmail(final String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User with email " + email + " is not found."));
    }
}
