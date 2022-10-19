package com.imdb.domain.service.impl;

import com.imdb.domain.user.model.dto.role.RoleChangeDTO;
import com.imdb.domain.user.model.dto.UserEntityRegisterDTO;
import com.imdb.domain.user.model.entity.User;
import com.imdb.domain.user.enums.Role;
import com.imdb.domain.user.mapping.UserEntityMapper;
import com.imdb.domain.user.repository.UserEntityRepository;
import com.imdb.domain.user.service.UserEntityService;
import com.imdb.exception.ObjectNotFoundException;
import com.imdb.domain.user.service.impl.UserEntityServiceImpl;
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
    private User userEntityTest;

    @Mock
    private UserEntityRepository userEntityRepositoryMock;
    @Mock
    private UserEntityMapper userEntityMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {
        userEntityServiceTest = new UserEntityServiceImpl(userEntityRepositoryMock, userEntityMapper, passwordEncoder);

        userEntityTest = new User();
    }

    @Test
    public void registerUser() {
        final UserEntityRegisterDTO userEntityRegisterDto = new UserEntityRegisterDTO();
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

        assertTrue(userEntityServiceTest.doesNotExistByEmail(USER_ENTITY_EMAIL));
    }

    @Test
    public void isNotExistByEmailShouldReturnFalseWhenUserWithEmailExist() {
        when(userEntityRepositoryMock.findByEmail(USER_ENTITY_EMAIL))
                .thenReturn(Optional.of(userEntityTest));

        assertFalse(userEntityServiceTest.doesNotExistByEmail(USER_ENTITY_EMAIL));
    }

    @Test
    public void changeUserRole() {
        final RoleChangeDTO roleChangeDto = new RoleChangeDTO();
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

        final User userEntity = userEntityServiceTest.getUserEntityByEmail(USER_ENTITY_EMAIL);

        assertEquals(userEntity.getEmail(), userEntityTest.getEmail());
    }

    @Test
    public void getUserEntityByEmailShouldThrowWhenUserWithEmailNotExist(){
        when(userEntityRepositoryMock.findByEmail(USER_ENTITY_EMAIL)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userEntityServiceTest.getUserEntityByEmail(USER_ENTITY_EMAIL));
    }
}