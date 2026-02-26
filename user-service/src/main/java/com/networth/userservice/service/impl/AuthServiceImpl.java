package com.networth.userservice.service.impl;

import com.networth.userservice.dto.LoginDto;
import com.networth.userservice.dto.LoginResponse;
import com.networth.userservice.dto.LogoutDto;
import com.networth.userservice.dto.TokenResponse;
import com.networth.userservice.entity.User;
import com.networth.userservice.exception.AuthenticationServiceException;
import com.networth.userservice.exception.UserNotFoundException;
import com.networth.userservice.exception.UserServiceException;
import com.networth.userservice.mapper.TokenResponseMapper;
import com.networth.userservice.repository.UserRepository;
import com.networth.userservice.service.AuthService;
import com.networth.userservice.service.KeycloakService;
import com.networth.userservice.util.HelperUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.mapstruct.factory.Mappers;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = Logger.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final HelperUtils helperUtils;
    private final KeycloakService keycloakService;
    private final TokenResponseMapper tokenResponseMapper = Mappers.getMapper(TokenResponseMapper.class);

    public AuthServiceImpl(UserRepository userRepository, HelperUtils helperUtils, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.helperUtils = helperUtils;
        this.keycloakService = keycloakService;
    }

    @Override
    public LoginResponse userLogin(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with Username: " + loginDto.getUsername()));

        helperUtils.validatePassword(loginDto.getPassword());

        try {
            TokenResponse tokenResponse = keycloakService.getUserAccessToken(loginDto);
            LoginResponse loginResponse = tokenResponseMapper.tokenResponseToLoginResponse(tokenResponse);
            loginResponse.setUserId(user.getUserId());
            return loginResponse;
        } catch (AuthenticationServiceException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Unexpected error during login", e);
            throw new UserServiceException("Unexpected error during login process.", e);
        }
    }

    @Override
    public void userLogout(LogoutDto logoutDto) {
        if (logoutDto.getAccessToken() != null && !logoutDto.getAccessToken().isEmpty()) {
            keycloakService.revokeAccessToken(logoutDto.getAccessToken());
        }

        if (logoutDto.getRefreshToken() != null && !logoutDto.getRefreshToken().isEmpty()) {
            keycloakService.logoutUser(logoutDto);
        }
    }
}
