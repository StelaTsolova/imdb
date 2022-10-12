package com.example.imdb.service.impl;

import com.example.imdb.model.entity.UserEntity;
import com.example.imdb.model.enums.Role;
import com.example.imdb.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    public static final String USER_DETAILS_EMAIL = "m@gamail.com";
    public static final String USER_DETAILS_PASSWORD = "123";

    private UserDetailsService userDetailsServiceTest;

    @Mock
    private UserEntityRepository userEntityRepositoryMock;

    @BeforeEach
    void init() {
        userDetailsServiceTest = new UserDetailsServiceImpl(userEntityRepositoryMock);
    }

    @Test
    public void loadUserByUsername() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(USER_DETAILS_EMAIL);
        userEntity.setPassword(USER_DETAILS_PASSWORD);
        userEntity.setRole(Role.USER);

        Mockito.when(userEntityRepositoryMock.findByEmail(USER_DETAILS_EMAIL))
                .thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userDetailsServiceTest.loadUserByUsername(USER_DETAILS_EMAIL);

        Assertions.assertEquals(userDetails.getUsername(), userEntity.getEmail());
        Assertions.assertEquals(userDetails.getPassword(), userEntity.getPassword());
    }

}