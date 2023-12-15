package com.example.manage_contacts.repository;

import com.example.manage_contacts.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User as u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}
