// src/main/java/ch/wiss/m295/service/InventoryService.java
package ch.wiss.m295.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.entity.*;
import ch.wiss.m295.repository.*;

// Zentrale Business-Logik für Inventar-Operationen (Take/Drop).
// Alle Änderungen erfolgen transaktional, um Inkonsistenzen
// bei parallelen Requests zu verhindern (ACID).

@Service
public class InventoryService {

    private final UserRepository userRepo;
    private final ItemRepository itemRepo;
    private final UserItemRepository userItemRepo;

    public InventoryService(UserRepository userRepo, ItemRepository itemRepo, UserItemRepository userItemRepo) {
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
        this.userItemRepo = userItemRepo;
    }

    /**
     * US3: Item nehmen (transaktional).
     */
    @Transactional
    public UserItem takeItem(Integer userId, Integer itemId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        Item item = itemRepo.findByIdForUpdate(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));

        if (item.getStock() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "out of stock");
        }

        item.setStock(item.getStock() - 1);
        itemRepo.save(item);

        UserItemId id = new UserItemId(userId, itemId);
        UserItem ui = userItemRepo.findById(id).orElseGet(() -> {
            UserItem nu = new UserItem();
            nu.setId(id);
            nu.setQuantity(0);
            return nu;
        });

        ui.setQuantity(ui.getQuantity() + 1);
        return userItemRepo.save(ui);
    }

    /**
     * US3: Item droppen (transaktional).
     */
    @Transactional
    public void dropItem(Integer userId, Integer itemId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }

        Item item = itemRepo.findByIdForUpdate(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));

        UserItemId id = new UserItemId(userId, itemId);
        UserItem ui = userItemRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "item not in inventory"));

        if (ui.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "item not in inventory");
        }

        ui.setQuantity(ui.getQuantity() - 1);
        if (ui.getQuantity() == 0) {
            userItemRepo.delete(ui);
        } else {
            userItemRepo.save(ui);
        }

        item.setStock(item.getStock() + 1);
        itemRepo.save(item);
    }

}
