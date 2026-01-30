package ch.wiss.m295.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import ch.wiss.m295.entity.Item;
import ch.wiss.m295.entity.Rarity;
import ch.wiss.m295.entity.User;
import ch.wiss.m295.repository.ItemRepository;
import ch.wiss.m295.repository.UserItemRepository;
import ch.wiss.m295.repository.UserRepository;

/**
 * US3 - Gleichzeitige Requests: nur einer bekommt das letzte Item.
 *
 * Hinweis: braucht eine Test-DB (z.B. H2) + ddl-auto=create-drop.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.show-sql=false"
})
class InventoryServiceConcurrencyIT {

    @Autowired private InventoryService inventoryService;
    @Autowired private UserRepository userRepo;
    @Autowired private ItemRepository itemRepo;
    @Autowired private UserItemRepository userItemRepo;

    @Test
    void onlyOneGetsLastItem_otherGets409() throws Exception {
        User user = new User();
        user.setUsername("u1");
        user.setEmail("u1@test.ch");
        user.setPasswordHash("HASH:x");
        final User u = userRepo.save(user);

        Item item = new Item();
        item.setCode("IT1");
        item.setName("Sword");
        item.setDescription("d");
        item.setRarity(Rarity.COMMON);
        item.setStock(1);
        final Item savedItem = itemRepo.save(item);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);

        AtomicInteger ok = new AtomicInteger(0);
        AtomicInteger conflict = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                start.await();
                inventoryService.takeItem(u.getUserId(), savedItem.getItemId());
                ok.incrementAndGet();
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode().value() == 409) conflict.incrementAndGet();
                else throw ex;
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                done.countDown();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        start.countDown();
        done.await();

        assertEquals(1, ok.get(), "genau ein Request soll erfolgreich sein");
        assertEquals(1, conflict.get(), "genau ein Request soll 409 bekommen");

        Item reloaded = itemRepo.findById(savedItem.getItemId()).orElseThrow();
        assertEquals(0, reloaded.getStock(), "stock muss 0 sein");

        var ui = userItemRepo.findById(new ch.wiss.m295.entity.UserItemId(u.getUserId(), savedItem.getItemId())).orElseThrow();
        assertEquals(1, ui.getQuantity(), "inventar soll quantity=1 haben");
    }
}
