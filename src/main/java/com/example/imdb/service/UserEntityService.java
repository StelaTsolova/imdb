package com.example.imdb.service;

import com.example.imdb.model.dto.RoleChangeDto;
import com.example.imdb.model.dto.UserEntityRegisterDto;
import com.example.imdb.model.entity.UserEntity;

public interface UserEntityService {

    void registerUser(UserEntityRegisterDto userEntityRegisterDto);

    boolean isNotExistByEmail(String email);

    void changeUserRole(RoleChangeDto roleChangeDto);

    UserEntity getUserEntityByEmail(String email);
}
