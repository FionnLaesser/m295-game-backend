package ch.wiss.m295.repository;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import ch.wiss.m295.entity.Achievement;
// Repository für Achievement-Daten.

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    boolean existsByCode(String code);

    @Query("""
        select a from Achievement a
        join ch.wiss.m295.entity.UserAchievement ua
          on ua.id.achievementId = a.achievementId
        where ua.id.userId = :userId
        """)
    List<Achievement> findUnlockedByUserId(@Param("userId") Integer userId);
}
