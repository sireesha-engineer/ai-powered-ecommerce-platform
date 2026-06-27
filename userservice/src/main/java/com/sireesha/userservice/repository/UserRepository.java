package com.sireesha.userservice.repository;

import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserStatus;
import com.sireesha.userservice.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    List<User> findByUserStatus(UserStatus userStatus);

    Optional<User> findByEmailAndUserStatus(String email, UserStatus userStatus);
}
