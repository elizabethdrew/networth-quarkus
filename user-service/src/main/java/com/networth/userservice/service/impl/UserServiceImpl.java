package com.networth.userservice.service.impl;

import com.networth.userservice.dto.RegisterDto;
import com.networth.userservice.dto.UpdateUserDto;
import com.networth.userservice.dto.UserOutput;
import com.networth.userservice.entity.User;
import com.networth.userservice.exception.InvalidInputException;
import com.networth.userservice.exception.KeycloakException;
import com.networth.userservice.exception.UserNotFoundException;
import com.networth.userservice.exception.UserServiceException;
import com.networth.userservice.mapper.UserMapper;
import com.networth.userservice.repository.UserRepository;
import com.networth.userservice.service.KeycloakService;
import com.networth.userservice.service.UserService;
import com.networth.userservice.util.HelperUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final HelperUtils helperUtils;
    private final KeycloakService keycloakService;

    public UserServiceImpl(UserRepository userRepository, HelperUtils helperUtils, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.helperUtils = helperUtils;
        this.keycloakService = keycloakService;
    }

    @Override
    public UserOutput getUser(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("Keycloak ID not found: " + keycloakId));
        return userMapper.toUserOutput(user);
    }

    @Override
    @Transactional
    public UserOutput registerUser(RegisterDto registerDto) {
        try {
            helperUtils.validateUsernameUnique(registerDto.getUsername());
            helperUtils.validateEmailUnique(registerDto.getEmail());
            helperUtils.validatePassword(registerDto.getPassword());

            String keycloakUserId = keycloakService.createUser(registerDto);
            User user = createUserEntity(registerDto, keycloakUserId);
            return userMapper.toUserOutput(user);
        } catch (InvalidInputException | KeycloakException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("An unexpected error occurred during user registration", e);
            throw new UserServiceException("An unexpected error occurred during registration", e);
        }
    }

    @Override
    @Transactional
    public UserOutput updateUser(String keycloakId, UpdateUserDto updateUserDto) {
        try {
            User user = userRepository.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new UserNotFoundException("Keycloak ID not found: " + keycloakId));

            String previousEmail = user.getEmail();
            userMapper.updateUserFromDto(updateUserDto, user);
            user.setDateUpdated(LocalDateTime.now());

            if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().equals(previousEmail)) {
                helperUtils.validateEmailUnique(updateUserDto.getEmail());
                keycloakService.updateEmailKeycloak(updateUserDto.getEmail(), keycloakId);
            }

            User savedUser = userRepository.save(user);
            return userMapper.toUserOutput(savedUser);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("An unexpected error occurred during update user", e);
            throw new UserServiceException("An unexpected error occurred during update user", e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("Keycloak ID not found: " + keycloakId));

        user.setActiveUser(false);
        userRepository.save(user);
    }

    private User createUserEntity(RegisterDto registerDto, String keycloakUserId) {
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setDateOpened(LocalDateTime.now());
        user.setDateUpdated(LocalDateTime.now());
        user.setActiveUser(true);
        user.setKeycloakId(keycloakUserId);

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserServiceException("Failed to save user entity", e);
        }
    }
}
