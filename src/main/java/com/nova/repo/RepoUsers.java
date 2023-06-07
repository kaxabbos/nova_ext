package com.nova.repo;

import com.nova.models.Users;
import com.nova.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepoUsers extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    List<Users> findAllByRole(Role role);
}
