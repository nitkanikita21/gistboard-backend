package com.nitkanikita.notes.repository;

import com.nitkanikita.notes.model.entity.User;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Option<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
