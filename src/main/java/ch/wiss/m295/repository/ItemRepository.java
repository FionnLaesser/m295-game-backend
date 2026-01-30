package ch.wiss.m295.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import ch.wiss.m295.entity.Item;
import jakarta.persistence.LockModeType;
// Repository für Item-Datenbankzugriffe.

public interface ItemRepository extends JpaRepository<Item, Integer> {
    boolean existsByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.itemId = :id")
    Optional<Item> findByIdForUpdate(@Param("id") Integer id);
}
