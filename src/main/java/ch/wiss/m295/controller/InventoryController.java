package ch.wiss.m295.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ch.wiss.m295.entity.UserItem;
import ch.wiss.m295.service.InventoryService;

// PLAYER-Controller für das Inventar.
// Ermöglicht Take- und Drop-Operationen auf Items eines Users.
// Die Logik ist transaktional im Service implementiert.

@RestController
@RequestMapping("/users")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * US3: Item nehmen.
     * Player-only (nicht eingeloggt -> 401, kein Player -> 403)
     */
    @PostMapping("/{userId}/items/{itemId}/take")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PLAYER')")
    public UserItem take(@PathVariable Integer userId, @PathVariable Integer itemId) {
        return inventoryService.takeItem(userId, itemId);
    }

    /**
     * US3: Item droppen.
     * Player-only (nicht eingeloggt -> 401, kein Player -> 403)
     */
    @PostMapping("/{userId}/items/{itemId}/drop")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('PLAYER')")
    public void drop(@PathVariable Integer userId, @PathVariable Integer itemId) {
        inventoryService.dropItem(userId, itemId);
    }

}
