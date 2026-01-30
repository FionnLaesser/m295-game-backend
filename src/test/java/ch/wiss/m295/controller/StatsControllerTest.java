package ch.wiss.m295.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import ch.wiss.m295.entity.Stats;
import ch.wiss.m295.service.StatsService;

/**
 * US2 - Stats speichern (SaveStats)
 */
@WebMvcTest(StatsController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({
    ch.wiss.m295.config.SecurityConfig.class,
    ch.wiss.m295.config.MethodSecurityConfig.class
})
class StatsControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private StatsService statsService;

    @Test
    void saveStats_notLoggedIn_401() throws Exception {
        mockMvc.perform(post("/users/1/stats"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void saveStats_success_201() throws Exception {
        Stats s = new Stats();
        s.setStatsId(10);
        s.setUserId(1);
        s.setLevel(3);
        s.setXp(120);
        s.setCoins(50);
        s.setSavedAt(Instant.parse("2026-01-29T12:00:00Z"));
        when(statsService.saveStats(eq(1))).thenReturn(s);

        mockMvc.perform(post("/users/1/stats"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statsId").value(10))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.level").value(3))
            .andExpect(jsonPath("$.xp").value(120))
            .andExpect(jsonPath("$.coins").value(50));

        verify(statsService).saveStats(1);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveStats_wrongRole_403() throws Exception {
        mockMvc.perform(post("/users/1/stats"))
            .andExpect(status().isForbidden());
    }
}
