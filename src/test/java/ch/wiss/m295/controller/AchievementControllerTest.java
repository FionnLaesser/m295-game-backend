package ch.wiss.m295.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import ch.wiss.m295.entity.Achievement;
import ch.wiss.m295.repository.AchievementRepository;

/**
 * US6 - Achievements verwalten (Admin)
 */
@WebMvcTest(AchievementController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({
    ch.wiss.m295.config.SecurityConfig.class,
    ch.wiss.m295.config.MethodSecurityConfig.class
})
class AchievementControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AchievementRepository repo;

    @Test
    void createAchievement_notLoggedIn_401() throws Exception {
        mockMvc.perform(post("/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"ACH1\",\"name\":\"First\",\"description\":\"d\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void createAchievement_notAdmin_403() throws Exception {
        mockMvc.perform(post("/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"ACH1\",\"name\":\"First\",\"description\":\"d\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createAchievement_invalidData_400() throws Exception {
        mockMvc.perform(post("/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"\",\"name\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createAchievement_codeUnique_conflict409() throws Exception {
        when(repo.existsByCode("ACH1")).thenReturn(true);

        mockMvc.perform(post("/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"ACH1\",\"name\":\"First\",\"description\":\"d\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createAchievement_success_201() throws Exception {
        when(repo.existsByCode("ACH1")).thenReturn(false);

        Achievement saved = new Achievement();
        saved.setAchievementId(9);
        saved.setCode("ACH1");
        saved.setName("First");
        saved.setDescription("d");

        when(repo.save(any(Achievement.class))).thenReturn(saved);

        mockMvc.perform(post("/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"ACH1\",\"name\":\"First\",\"description\":\"d\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.achievementId").value(9))
            .andExpect(jsonPath("$.code").value("ACH1"))
            .andExpect(jsonPath("$.name").value("First"));
    }
}
