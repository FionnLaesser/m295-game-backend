// src/main/java/ch/wiss/m295/controller/UserAchievementsViewController.java
package ch.wiss.m295.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ch.wiss.m295.entity.Achievement;
import ch.wiss.m295.repository.AchievementRepository;

// Read-only Controller zur Anzeige aller freigeschalteten Achievements eines Users.
// Dient ausschliesslich der Abfrage, nicht der Manipulation.

@RestController
@RequestMapping("/users")
public class UserAchievementsViewController {

    private final AchievementRepository achievementRepo;

    public UserAchievementsViewController(AchievementRepository achievementRepo) {
        this.achievementRepo = achievementRepo;
    }

    /**
     * US4: eigene Achievements anzeigen.
     */
    @GetMapping("/{userId}/achievements")
    @PreAuthorize("hasRole('PLAYER')")
    public List<Achievement> myAchievements(@PathVariable Integer userId) {
        return achievementRepo.findUnlockedByUserId(userId);
    }
}
