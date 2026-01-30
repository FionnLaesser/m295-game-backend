package ch.wiss.m295.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.entity.UserAchievement;
import ch.wiss.m295.entity.UserAchievementId;
import ch.wiss.m295.repository.AchievementRepository;
import ch.wiss.m295.repository.UserAchievementRepository;
import ch.wiss.m295.repository.UserRepository;

/**
 * REST controller for unlocking achievements for users.
 */
@RestController
@RequestMapping("/users")
public class UserAchievementController {

    private final UserRepository userRepo;
    private final AchievementRepository achievementRepo;
    private final UserAchievementRepository userAchievementRepo;

    public UserAchievementController(
            UserRepository userRepo,
            AchievementRepository achievementRepo,
            UserAchievementRepository userAchievementRepo
    ) {
        this.userRepo = userRepo;
        this.achievementRepo = achievementRepo;
        this.userAchievementRepo = userAchievementRepo;
    }

    /**
     * Unlocks an achievement for a user (only once).
     * US: Achievement nur einmal pro User.
     *
     * Player-only:
     *  - nicht eingeloggt -> 401
     *  - kein Player -> 403
     *
     * @param userId user id
     * @param achievementId achievement id
     * @return created user_achievement row
     */
    @PostMapping("/{userId}/achievements/{achievementId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PLAYER')")
    public UserAchievement unlock(@PathVariable Integer userId, @PathVariable Integer achievementId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
        if (!achievementRepo.existsById(achievementId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "achievement not found");
        }

        UserAchievementId id = new UserAchievementId(userId, achievementId);

        if (userAchievementRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "achievement already unlocked");
        }

        UserAchievement ua = new UserAchievement();
        ua.setId(id);

        return userAchievementRepo.save(ua);
    }
}
