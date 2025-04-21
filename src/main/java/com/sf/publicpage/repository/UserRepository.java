package com.sf.publicpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.publicpage.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // User findByUsername(String username);
    Optional<User> findByUsername(String username);
}

// public interface UserRepository extends JpaRepository<User, Long> {
// // แก้ไขให้เป็น Optional<User> เพื่อใช้ orElseThrow()
// Optional<User> findByUsername(String username);
// }