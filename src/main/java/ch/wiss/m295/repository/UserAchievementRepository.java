package ch.wiss.m295.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.wiss.m295.entity.UserAchievement;
import ch.wiss.m295.entity.UserAchievementId;
// Repository für freigeschaltete Achievements eines Users.

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UserAchievementId> {
}
