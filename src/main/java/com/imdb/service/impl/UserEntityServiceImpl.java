package com.imdb.service.impl;

import com.imdb.model.dto.RoleChangeDto;
import com.imdb.model.dto.UserEntityRegisterDto;
import com.imdb.model.entity.UserEntity;
import com.imdb.model.enums.Role;
import com.imdb.model.mapping.UserEntityMapper;
import com.imdb.repository.UserEntityRepository;
import com.imdb.service.UserEntityService;
import com.imdb.web.exception.ObjectNotFoundException;
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
    public void registerUser(UserEntityRegisterDto userEntityRegisterDto) {
        UserEntity userEntity = userEntityMapper.mapUserEntityRegisterDtoToUserEntity(userEntityRegisterDto);
        userEntity.setPassword(this.passwordEncoder.encode(userEntityRegisterDto.getPassword()));
        userEntity.setRole(Role.USER);

        this.repository.save(userEntity);
    }

    @Override
    public boolean isNotExistByEmail(String email) {
        return repository.findByEmail(email).isEmpty();
    }

    @Override
    public void changeUserRole(RoleChangeDto roleChangeDto) {
        UserEntity user = getUserEntityByEmail(roleChangeDto.getUserEmail());
        user.setRole(Role.valueOf(roleChangeDto.getRole().toUpperCase()));

        repository.save(user);
    }

    @Override
    public UserEntity getUserEntityByEmail(String email) {
        return this.repository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User with email " + email + " is not found."));
    }
}
