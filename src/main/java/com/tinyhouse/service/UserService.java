package com.tinyhouse.service;

import com.tinyhouse.dto.response.UserResponse;
import com.tinyhouse.entity.User;
import com.tinyhouse.enums.Role;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.UserMapper;
import com.tinyhouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor @Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public UserResponse getUserByEmail(String email) { return userMapper.toResponse(findUserByEmail(email)); }

    public Page<UserResponse> getAllUsers(Pageable pageable) { return userRepository.findByDeletedFalse(pageable).map(userMapper::toResponse); }

    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) { return userRepository.findByRoleAndDeletedFalse(role, pageable).map(userMapper::toResponse); }

    public Page<UserResponse> searchUsers(String query, Pageable pageable) { return userRepository.searchUsers(query, pageable).map(userMapper::toResponse); }

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setActive(!user.isActive()); userRepository.save(user);
        log.info("User {} status toggled to {}", id, user.isActive());
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse changeUserRole(Long id, Role role) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setRole(role); userRepository.save(user);
        log.info("User {} role changed to {}", id, role);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setDeleted(true); userRepository.save(user);
        log.info("User {} soft deleted", id);
    }
}
