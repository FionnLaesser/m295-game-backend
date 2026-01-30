package ch.wiss.m295.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import ch.wiss.m295.entity.Item;
import ch.wiss.m295.entity.Rarity;
import ch.wiss.m295.repository.ItemRepository;

/**
 * US5 - Item verwalten (Admin)
 */
@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({
    ch.wiss.m295.config.SecurityConfig.class,
    ch.wiss.m295.config.MethodSecurityConfig.class
})
class ItemControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ItemRepository repo;

    @Test
    void createItem_notLoggedIn_401() throws Exception {
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"IT1\",\"name\":\"Sword\",\"rarity\":\"COMMON\",\"stock\":3}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void createItem_notAdmin_403() throws Exception {
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"IT1\",\"name\":\"Sword\",\"rarity\":\"COMMON\",\"stock\":3}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createItem_invalidData_400() throws Exception {
        // missing code + missing rarity
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"\",\"name\":\"\",\"stock\":-1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createItem_codeUnique_conflict409() throws Exception {
        when(repo.existsByCode("IT1")).thenReturn(true);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"IT1\",\"name\":\"Sword\",\"rarity\":\"COMMON\",\"stock\":3}"))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createItem_success_201() throws Exception {
        when(repo.existsByCode("IT1")).thenReturn(false);

        Item saved = new Item();
        saved.setItemId(7);
        saved.setCode("IT1");
        saved.setName("Sword");
        saved.setDescription("basic");
        saved.setRarity(Rarity.COMMON);
        saved.setStock(3);
        when(repo.save(any(Item.class))).thenReturn(saved);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"IT1\",\"name\":\"Sword\",\"description\":\"basic\",\"rarity\":\"COMMON\",\"stock\":3}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.itemId").value(7))
            .andExpect(jsonPath("$.code").value("IT1"))
            .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateItem_setStock_200() throws Exception {
        Item db = new Item();
        db.setItemId(7);
        db.setCode("IT1");
        db.setName("Sword");
        db.setRarity(Rarity.COMMON);
        db.setStock(3);

        when(repo.findById(7)).thenReturn(Optional.of(db));
        when(repo.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/items/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stock\":5}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.itemId").value(7))
            .andExpect(jsonPath("$.stock").value(5));
    }
}
