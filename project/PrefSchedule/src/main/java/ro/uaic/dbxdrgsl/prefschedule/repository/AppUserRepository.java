package ro.uaic.dbxdrgsl.prefschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.dbxdrgsl.prefschedule.model.AppUser;

import java.util.Optional;

/**
 * Repository for AppUser entities.
 * Provides database access for user authentication and management.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    
    /**
     * Find a user by username.
     */
    Optional<AppUser> findByUsername(String username);
    
    /**
     * Check if a username already exists.
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists.
     */
    boolean existsByEmail(String email);
}
