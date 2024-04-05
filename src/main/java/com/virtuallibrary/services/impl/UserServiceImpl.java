package com.virtuallibrary.services.impl;

import com.virtuallibrary.entities.Role;
import com.virtuallibrary.entities.User;
import com.virtuallibrary.repositories.RoleRepository;
import com.virtuallibrary.repositories.UserRepository;
import com.virtuallibrary.services.LoanService;
import com.virtuallibrary.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LoanService loanService;

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    @Override
    public User save(User user) {

        Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        optionalRole.ifPresent(roles::add);

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByEnabledTrue() {
        return userRepository.findByEnabledTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByEnabledFalse() {
        return userRepository.findByEnabledFalse();
    }

    @Transactional
    @Override
    public void activate(Long id) {
        Optional<User> userOptional = findById(id);
        userOptional.ifPresent(user -> {
            if (!user.isEnabled()) {
                user.setEnabled(true);
                save(user);
                loanService.findByUser(user).forEach(loan -> loanService.activate(loan.getId()));
            }
        });
    }

    @Transactional
    @Override
    public void deactivate(Long id) {
        Optional<User> userOptional = findById(id);
        userOptional.ifPresent(user -> {
            if (user.isEnabled()) {
                user.setEnabled(false);
                save(user);
                loanService.findByUser(user).forEach(loan -> loanService.deactivate(loan.getId()));
            }
        });
    }
}
