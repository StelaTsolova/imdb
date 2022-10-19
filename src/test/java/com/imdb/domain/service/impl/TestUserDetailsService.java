package com.imdb.domain.service.impl;

import com.imdb.domain.user.model.entity.User;
import com.imdb.domain.user.enums.Role;
import com.imdb.domain.user.repository.UserEntityRepository;
import com.imdb.domain.user.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestUserDetailsService {
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
        final User userEntity = new User();
        userEntity.setEmail(USER_DETAILS_EMAIL);
        userEntity.setPassword(USER_DETAILS_PASSWORD);
        userEntity.setRole(Role.USER);

        when(userEntityRepositoryMock.findByEmail(USER_DETAILS_EMAIL)).thenReturn(Optional.of(userEntity));

        final UserDetails userDetails = userDetailsServiceTest.loadUserByUsername(USER_DETAILS_EMAIL);

        assertEquals(userDetails.getUsername(), userEntity.getEmail());
        assertEquals(userDetails.getPassword(), userEntity.getPassword());
    }

}