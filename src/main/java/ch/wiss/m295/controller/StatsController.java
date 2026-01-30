package ch.wiss.m295.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ch.wiss.m295.entity.Stats;
import ch.wiss.m295.service.StatsService;

// Controller für Stats-History.
// Erstellt unveränderliche Snapshots des aktuellen Spielstands.
// Es existieren bewusst keine Update- oder Delete-Endpunkte.

@RestController
@RequestMapping("/users")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/{userId}/stats")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PLAYER')")
    public Stats save(@PathVariable Integer userId) {
        return statsService.saveStats(userId);
    }

    @GetMapping("/{userId}/stats")
    @PreAuthorize("hasRole('PLAYER')")
    public List<Stats> list(@PathVariable Integer userId) {
        return statsService.listByUser(userId);
    }
}
