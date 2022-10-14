package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.RoleChangeDto;
import com.imdb.domain.model.dto.UserEntityRegisterDto;
import com.imdb.domain.model.entity.UserEntity;
import com.imdb.domain.model.enums.Role;
import com.imdb.domain.model.mapping.UserEntityMapper;
import com.imdb.domain.repository.UserEntityRepository;
import com.imdb.domain.service.UserEntityService;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestUserEntityService {
    public static final String USER_ENTITY_EMAIL = "u@gmail.com";
    public static final String USER_ENTITY_PASSWORD = "123456";

    private UserEntityService userEntityServiceTest;
    private UserEntity userEntityTest;

    @Mock
    private UserEntityRepository userEntityRepositoryMock;
    @Mock
    private UserEntityMapper userEntityMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {
        userEntityServiceTest = new UserEntityServiceImpl(userEntityRepositoryMock, userEntityMapper, passwordEncoder);

        userEntityTest = new UserEntity();
    }

    @Test
    public void registerUser() {
        final UserEntityRegisterDto userEntityRegisterDto = new UserEntityRegisterDto();
        userEntityRegisterDto.setPassword(USER_ENTITY_PASSWORD);

        when(userEntityMapper.mapUserEntityRegisterDtoToUserEntity(userEntityRegisterDto))
                .thenReturn(userEntityTest);

        assertNull(userEntityTest.getRole());

        userEntityServiceTest.registerUser(userEntityRegisterDto);

        assertEquals(userEntityTest.getRole(), Role.USER);
    }

    @Test
    public void isNotExistByEmailShouldReturnTrueWhenUserWithEmailNotExist() {
        when(userEntityRepositoryMock.findByEmail(USER_ENTITY_EMAIL)).thenReturn(Optional.empty());

        assertTrue(userEntityServiceTest.isNotExistByEmail(USER_ENTITY_EMAIL));
    }

    @Test
    public void isNotExistByEmailShouldReturnFalseWhenUserWithEmailExist() {
        when(userEntityRepositoryMock.findByEmail(USER_ENTITY_EMAIL))
                .thenReturn(Optional.of(userEntityTest));

        assertFalse(userEntityServiceTest.isNotExistByEmail(USER_ENTITY_EMAIL));
    }

    @Test
    public void changeUserRole() {
        final RoleChangeDto roleChangeDto = new RoleChangeDto();
        roleChangeDto.setUserEmail(USER_ENTITY_EMAIL);
        roleChangeDto.setRole("usEr");

        when(userEntityRepositoryMock.findByEmail(USER_ENTITY_EMAIL))
                .thenReturn(Optional.of(userEntityTest));

        assertNull(userEntityTest.getRole());

        userEntityServiceTest.changeUserRole(roleChangeDto);

        assertEquals(userEntityTest.getRole(), Role.USER);
    }

    @Test
    public void getUserEntityByEmail(){
        when(userEntityRepositoryMock.findByEmail(USER_ENTITY_EMAIL)).thenReturn(Optional.of(userEntityTest));

        final UserEntity userEntity = userEntityServiceTest.getUserEntityByEmail(USER_ENTITY_EMAIL);

        assertEquals(userEntity.getEmail(), userEntityTest.getEmail());
    }

    @Test
    public void getUserEntityByEmailShouldThrowWhenUserWithEmailNotExist(){
        when(userEntityRepositoryMock.findByEmail(USER_ENTITY_EMAIL)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userEntityServiceTest.getUserEntityByEmail(USER_ENTITY_EMAIL));
    }
}