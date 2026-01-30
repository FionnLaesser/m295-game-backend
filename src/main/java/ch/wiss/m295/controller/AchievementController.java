package ch.wiss.m295.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.dto.AchievementCreateRequest;
import ch.wiss.m295.dto.AchievementUpdateRequest;
import ch.wiss.m295.entity.Achievement;
import ch.wiss.m295.repository.AchievementRepository;
import jakarta.validation.Valid;

// ADMIN-Controller für Achievements.
// Achievements definieren erreichbare Ziele im Spiel,
// die später pro User einmalig freigeschaltet werden können.

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    private final AchievementRepository repo;

    public AchievementController(AchievementRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Achievement> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Achievement getById(@PathVariable Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "achievement not found"));
    }

    /**
     * US6: Admin only
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Achievement create(@Valid @RequestBody AchievementCreateRequest req) {
        if (repo.existsByCode(req.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "code already exists");
        }
        Achievement a = new Achievement();
        a.setCode(req.getCode());
        a.setName(req.getName());
        a.setDescription(req.getDescription());
        return repo.save(a);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Achievement update(@PathVariable Integer id, @Valid @RequestBody AchievementUpdateRequest req) {
        Achievement a = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "achievement not found"));

        if (req.getName() != null && !req.getName().isBlank()) a.setName(req.getName());
        if (req.getDescription() != null) a.setDescription(req.getDescription());
        return repo.save(a);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "achievement not found");
        }
        repo.deleteById(id);
    }
}
