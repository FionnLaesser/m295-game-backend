package ch.wiss.m295.entity;

import java.time.Instant;

import jakarta.persistence.*;

// Join-Entity zwischen User und Achievement.
// Der zusammengesetzte Primary Key verhindert doppelte Freischaltungen.

@Entity
@Table(name = "user_achievement")
public class UserAchievement {

    @EmbeddedId
    private UserAchievementId id;

    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private Instant unlockedAt = Instant.now();

    public UserAchievementId getId() { return id; }
    public void setId(UserAchievementId id) { this.id = id; }

    public Instant getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(Instant unlockedAt) { this.unlockedAt = unlockedAt; }
}
