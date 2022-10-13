//package com.example.imdb.service.impl;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Objects;
//
//import com.example.imdb.model.entity.UserEntity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//public class UserDetailsImpl implements UserDetails {
//
//    private static final long serialVersionUID = 1L;
//    private final Long id;
//    private final String email;
//    private final String password;
//    @JsonIgnore
//    private final List<GrantedAuthority> authorities;
//
//    public UserDetailsImpl(Long id, String email, String password,
//                           List<GrantedAuthority> authorities) {
//        this.id = id;
//        this.email = email;
//        this.password = password;
//        this.authorities = authorities;
//    }
//
//    public static UserDetailsImpl build(UserEntity userEntity) {
//        List<GrantedAuthority> authorities = List.of(
//                new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name()));
//
//        return new UserDetailsImpl(
//                userEntity.getId(),
//                userEntity.getEmail(),
//                userEntity.getPassword(),
//                authorities);
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return this.authorities;
//    }
//
//    public Long getId() {
//        return this.id;
//    }
//
//    @Override
//    public String getPassword() {
//        return this.password;
//    }
//
//    @Override
//    public String getUsername() {
//        return this.email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o)
//            return true;
//        if (o == null || getClass() != o.getClass())
//            return false;
//        UserDetailsImpl user = (UserDetailsImpl) o;
//        return Objects.equals(id, user.id);
//    }
//}