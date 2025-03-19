package com.example.lamashop.service;

import com.example.lamashop.dto.RegisterRequestDto;
import com.example.lamashop.model.User;
import com.example.lamashop.model.enumType.RoleName;
import com.example.lamashop.repository.UserRepository;
import com.example.lamashop.exception.ResourceNotFoundException;
import com.example.lamashop.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

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

}
