package com.example.lamashop.service;

import com.example.lamashop.dto.CustomerProfileDto;
import com.example.lamashop.dto.RegisterRequestDto;
import com.example.lamashop.dto.UserProfileDto;
import com.example.lamashop.mapper.UserMapper;
import com.example.lamashop.model.User;
import com.example.lamashop.model.enumType.RoleName;
import com.example.lamashop.repository.UserRepository;
import com.example.lamashop.exception.ResourceNotFoundException;
import com.example.lamashop.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final CustomerProfileService customerProfileService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User createUser(RegisterRequestDto request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(RoleName.CUSTOMER);

        return userRepository.save(user);
    }

    public User validateUserCredentials(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow((ResourceNotFoundException::forUser));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new ValidationException();
        }

        return user;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserProfileDto getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(ResourceNotFoundException::forUser);

        if (user.getRole() == RoleName.CUSTOMER) {
            logger.info("Get CUSTOMER Profile with id {}", userId);
            return customerProfileService.getCustomerProfile(user);
        } else {

            return userMapper.toUserProfileDto(user);
        }
    }

    public CustomerProfileDto updateShippingAddress(String userId, String newAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(ResourceNotFoundException::forUser);
        user.setShippingAddress(newAddress);
        userRepository.save(user);

        return customerProfileService.getCustomerProfile(user);
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public RoleName getUserRole(String userId) {
        User user = findById(userId)
                .orElseThrow(ResourceNotFoundException::forUser);

        return user.getRole();
    }

    public List<UserProfileDto> getPagedUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> result;
        if (query == null || query.isBlank()) {
            result = userRepository.findAll(pageable);
        } else {
            result = userRepository.findByEmailContainingIgnoreCase(query, pageable);
        }

        return result
                .stream()
                .map(userMapper::toUserProfileDto)
                .collect(Collectors.toList());
    }

}
