package ch.wiss.m295.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.dto.UserCreateRequest;
import ch.wiss.m295.dto.UserUpdateRequest;
import ch.wiss.m295.entity.User;
import ch.wiss.m295.repository.UserRepository;
import jakarta.validation.Valid;

// REST-Controller zur Verwaltung von Usern.
// Stellt CRUD-Endpunkte bereit und validiert Eingaben über DTOs.
// Business-Logik ist bewusst in den Service ausgelagert.

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<User> getAll() {
        return repo.findAll();
    }

    @PostMapping
@ResponseStatus(HttpStatus.CREATED)
public User create(@Valid @RequestBody UserCreateRequest req) {
    if (repo.existsByUsername(req.getUsername())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "username already exists");
    }
    if (repo.existsByEmail(req.getEmail())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "email already exists");
    }

    User u = new User();
    u.setUsername(req.getUsername());
    u.setEmail(req.getEmail());

    // "hash" placeholder (kein echtes Hashing für dieses Beispiel)
    u.setPasswordHash("HASH:" + req.getPassword());

    return repo.save(u);
}

/**
 * Returns one user by id.
 *
 * @param id user id
 * @return user
 */
@GetMapping("/{id}")
public User getById(@PathVariable Integer id) {
    return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
}
/**
 * Deletes a user by id.
 *
 * @param id user id
 */
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Integer id) {
    if (!repo.existsById(id)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
    }
    repo.deleteById(id);
}
/**
 * Updates a user by id.
 *
 * @param id user id
 * @param req update request
 * @return updated user
 */
@PutMapping("/{id}")
public User update(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest req) {
    User u = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

    if (req.getEmail() != null && !req.getEmail().isBlank()) {
        if (repo.existsByEmail(req.getEmail()) && !req.getEmail().equals(u.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already exists");
        }
        u.setEmail(req.getEmail());
    }
    if (req.getLevel() != null) u.setLevel(req.getLevel());
    if (req.getXp() != null) u.setXp(req.getXp());
    if (req.getCoins() != null) u.setCoins(req.getCoins());

    return repo.save(u);
}

}
