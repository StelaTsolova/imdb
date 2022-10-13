package com.imdb.domain.service;

import com.imdb.domain.model.dto.RoleChangeDto;
import com.imdb.domain.model.dto.UserEntityRegisterDto;
import com.imdb.domain.model.entity.UserEntity;

public interface UserEntityService {

    void registerUser(UserEntityRegisterDto userEntityRegisterDto);

    boolean isNotExistByEmail(String email);

    void changeUserRole(RoleChangeDto roleChangeDto);

    UserEntity getUserEntityByEmail(String email);
}
