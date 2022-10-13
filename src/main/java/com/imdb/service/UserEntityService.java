package com.imdb.service;

import com.imdb.model.dto.RoleChangeDto;
import com.imdb.model.dto.UserEntityRegisterDto;
import com.imdb.model.entity.UserEntity;

public interface UserEntityService {

    void registerUser(UserEntityRegisterDto userEntityRegisterDto);

    boolean isNotExistByEmail(String email);

    void changeUserRole(RoleChangeDto roleChangeDto);

    UserEntity getUserEntityByEmail(String email);
}
