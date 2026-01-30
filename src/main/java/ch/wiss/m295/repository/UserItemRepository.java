package ch.wiss.m295.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ch.wiss.m295.entity.UserItem;
import ch.wiss.m295.entity.UserItemId;
// Repository für Inventar-Zugriffe (User ↔ Item).

public interface UserItemRepository extends JpaRepository<UserItem, UserItemId> {
}
