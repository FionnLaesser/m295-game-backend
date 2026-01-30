package ch.wiss.m295.entity;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Unveränderlicher Snapshot des Spielstands eines Users
// zu einem bestimmten Zeitpunkt (History).

@Entity
@Table(name = "stats")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id")
    private Integer statsId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Min(1)
    @Column(nullable = false)
    private int level;

    @Min(0)
    @Column(nullable = false)
    private int xp;

    @Min(0)
    @Column(nullable = false)
    private int coins;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private Instant savedAt = Instant.now();

    public Integer getStatsId() { return statsId; }
    public void setStatsId(Integer statsId) { this.statsId = statsId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }

    public Instant getSavedAt() { return savedAt; }
    public void setSavedAt(Instant savedAt) { this.savedAt = savedAt; }
}
