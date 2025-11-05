package com.personalproject.airecipegenerator.Repository;

import com.personalproject.airecipegenerator.Dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}