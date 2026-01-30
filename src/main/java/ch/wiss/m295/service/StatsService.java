package ch.wiss.m295.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.entity.Stats;
import ch.wiss.m295.entity.User;
import ch.wiss.m295.repository.StatsRepository;
import ch.wiss.m295.repository.UserRepository;

// Service für Stats-History.
// Jeder Save erzeugt einen neuen Snapshot (INSERT-only).
// Bereits gespeicherte Stats werden niemals verändert oder gelöscht.

@Service
public class StatsService {

    private final UserRepository userRepo;
    private final StatsRepository statsRepo;

    public StatsService(UserRepository userRepo, StatsRepository statsRepo) {
        this.userRepo = userRepo;
        this.statsRepo = statsRepo;
    }

    /**
     * Creates a new stats snapshot for the given user.
     * The client does not send any values; they are copied from the current user.
     *
     * @param userId user id
     * @return created stats snapshot
     */
    @Transactional
    public Stats saveStats(Integer userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        Stats s = new Stats();
        s.setUserId(u.getUserId());
        s.setLevel(u.getLevel());
        s.setXp(u.getXp());
        s.setCoins(u.getCoins());

        return statsRepo.save(s);
    }

    /**
     * Returns stats history for a user (newest first).
     *
     * @param userId user id
     * @return list of snapshots
     */
    @Transactional(readOnly = true)
    public List<Stats> listByUser(Integer userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
        return statsRepo.findByUserIdOrderBySavedAtDesc(userId);
    }
}