package ch.wiss.m295.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import ch.wiss.m295.entity.UserItem;
import ch.wiss.m295.entity.UserItemId;
import ch.wiss.m295.service.InventoryService;

/**
 * US3 - Item nehmen (transaktional)
 */
@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({
    ch.wiss.m295.config.SecurityConfig.class,
    ch.wiss.m295.config.MethodSecurityConfig.class
})
class InventoryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private InventoryService inventoryService;

    @Test
    void takeItem_notLoggedIn_401() throws Exception {
        mockMvc.perform(post("/users/1/items/7/take"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void takeItem_wrongRole_403() throws Exception {
        mockMvc.perform(post("/users/1/items/7/take"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void takeItem_success_201_andStockReducedInService() throws Exception {
        UserItem ui = new UserItem();
        ui.setId(new UserItemId(1, 7));
        ui.setQuantity(1);

        when(inventoryService.takeItem(eq(1), eq(7))).thenReturn(ui);

        mockMvc.perform(post("/users/1/items/7/take"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id.userId").value(1))
            .andExpect(jsonPath("$.id.itemId").value(7))
            .andExpect(jsonPath("$.quantity").value(1));

        verify(inventoryService).takeItem(1, 7);
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void takeItem_outOfStock_409() throws Exception {
        when(inventoryService.takeItem(eq(1), eq(7)))
            .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "out of stock"));

        mockMvc.perform(post("/users/1/items/7/take"))
            .andExpect(status().isConflict());
    }


    @Test
    void dropItem_notLoggedIn_401() throws Exception {
        mockMvc.perform(post("/users/1/items/7/drop"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void dropItem_wrongRole_403() throws Exception {
        mockMvc.perform(post("/users/1/items/7/drop"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void dropItem_success_200() throws Exception {
        mockMvc.perform(post("/users/1/items/7/drop"))
            .andExpect(status().isOk());

        verify(inventoryService).dropItem(1, 7);
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void dropItem_notInInventory_409() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "item not in inventory"))
            .when(inventoryService).dropItem(eq(1), eq(7));
        mockMvc.perform(post("/users/1/items/7/drop"))
            .andExpect(status().isConflict());
    }
}