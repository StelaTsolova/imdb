package com.example.imdb.config;

import com.example.imdb.config.filter.CustomAuthenticationFilter;
import com.example.imdb.config.filter.CustomAuthorizationFilter;
import com.example.imdb.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.imdb.util.UtilClass.*;

@Configuration
@RequiredArgsConstructor
public class ApplicationSecurityConfiguration {

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(this.userDetailsService);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        httpSecurity
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
             .and()
                .authenticationManager(authenticationManager)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, PATH_INDEX, PATH_MOVIES,
                        PATH_MOVIES + PATH_ID, PATH_MOVIES + PATH_SEARCH).permitAll()
                .antMatchers(PATH_LOGIN, PATH_REGISTER).anonymous()
                .antMatchers(HttpMethod.PUT, PATH_MOVIES + PATH_ID, PATH_CONTROL).hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.GET, PATH_MOVIES + PATH_MY, PATH_TOKEN_REFRESH).authenticated()
                .antMatchers(HttpMethod.POST, PATH_MOVIES,
                        PATH_MOVIES + PATH_ID + PATH_PICTURE).authenticated()
                .antMatchers(HttpMethod.DELETE, PATH_MOVIES + PATH_ID).authenticated()
             .and()
                .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new CustomAuthenticationFilter(authenticationManager));

        return httpSecurity.build();
    }
}
