package ch.wiss.m295.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Composite key for user_achievement.
 */
@Embeddable
public class UserAchievementId implements Serializable {

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "achievement_id")
    private Integer achievementId;

    public UserAchievementId() {}

    public UserAchievementId(Integer userId, Integer achievementId) {
        this.userId = userId;
        this.achievementId = achievementId;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getAchievementId() { return achievementId; }
    public void setAchievementId(Integer achievementId) { this.achievementId = achievementId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAchievementId other)) return false;
        return Objects.equals(userId, other.userId) && Objects.equals(achievementId, other.achievementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, achievementId);
    }
}
