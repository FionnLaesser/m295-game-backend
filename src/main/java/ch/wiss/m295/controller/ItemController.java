package ch.wiss.m295.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.dto.ItemCreateRequest;
import ch.wiss.m295.dto.ItemUpdateRequest;
import ch.wiss.m295.entity.Item;
import ch.wiss.m295.repository.ItemRepository;
import jakarta.validation.Valid;

// ADMIN-Controller für die Verwaltung von Items.
// Ermöglicht Anlegen, Ändern und Löschen von Items mit globalem Stock.

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository repo;

    public ItemController(ItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Item> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Item getById(@PathVariable Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    /**
     * US5: Admin only
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Item create(@Valid @RequestBody ItemCreateRequest req) {
        if (repo.existsByCode(req.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "code already exists");
        }
        Item item = new Item();
        item.setCode(req.getCode());
        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setRarity(req.getRarity());
        item.setStock(req.getStock() == null ? 0 : req.getStock());
        return repo.save(item);
    }

    /**
     * US5: Admin only
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Item update(@PathVariable Integer id, @Valid @RequestBody ItemUpdateRequest req) {
        Item item = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));

        if (req.getName() != null && !req.getName().isBlank()) item.setName(req.getName());
        if (req.getDescription() != null) item.setDescription(req.getDescription());
        if (req.getRarity() != null) item.setRarity(req.getRarity());
        if (req.getStock() != null) item.setStock(req.getStock());

        return repo.save(item);
    }

    /**
     * Admin only
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
        }
        repo.deleteById(id);
    }
}
