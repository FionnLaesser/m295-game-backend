package ch.wiss.m295.repository;

import ch.wiss.m295.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
// JPA-Repository für User-Zugriffe.
// Kapselt alle Datenbankabfragen für User.

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
