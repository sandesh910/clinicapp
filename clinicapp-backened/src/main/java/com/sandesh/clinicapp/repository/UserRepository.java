package com.sandesh.clinicapp.repository;

import com.sandesh.clinicapp.model.Role;
import com.sandesh.clinicapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
