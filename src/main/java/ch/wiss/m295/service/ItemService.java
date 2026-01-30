package ch.wiss.m295.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.entity.Item;
import ch.wiss.m295.repository.ItemRepository;

// Service für Item-Verwaltung.
// Verantwortlich für Konsistenz des globalen Item-Bestands.

@Service
public class ItemService {

    private final ItemRepository repo;

    public ItemService(ItemRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Item create(Item i) {
        if (repo.existsByCode(i.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "code already exists");
        }
        return repo.save(i);
    }

    // Keine eigene @Transactional-Logik nötig
    public Item get(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    @Transactional
    public Item update(Integer id, Item in) {
        Item db = get(id);
        db.setName(in.getName());
        db.setDescription(in.getDescription());
        db.setRarity(in.getRarity());
        db.setStock(in.getStock());
        return repo.save(db);
    }

    @Transactional
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found");
        }
        repo.deleteById(id);
    }

    /** Stock-Logik: einmal "kaufen" */
    @Transactional
    public Item takeOne(Integer id) {
        Item i = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));

        if (i.getStock() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "out of stock");
        }

        i.setStock(i.getStock() - 1);
        return repo.save(i);
    }
}
