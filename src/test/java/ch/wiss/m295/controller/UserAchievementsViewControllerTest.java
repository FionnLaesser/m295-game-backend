package ch.wiss.m295.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import ch.wiss.m295.entity.Achievement;
import ch.wiss.m295.repository.AchievementRepository;

/**
 * US4 - Achievements sehen (nur eigene, leere Liste ok)
 */
@WebMvcTest(UserAchievementsViewController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({
    ch.wiss.m295.config.SecurityConfig.class,
    ch.wiss.m295.config.MethodSecurityConfig.class
})
class UserAchievementsViewControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AchievementRepository achievementRepo;

    @Test
    void myAchievements_notLoggedIn_401() throws Exception {
        mockMvc.perform(get("/users/1/achievements"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void myAchievements_emptyList_200() throws Exception {
        when(achievementRepo.findUnlockedByUserId(eq(1))).thenReturn(List.of());

        mockMvc.perform(get("/users/1/achievements"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

        verify(achievementRepo).findUnlockedByUserId(1);
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void myAchievements_returnsOnlyUnlocked_200() throws Exception {
        Achievement a = new Achievement();
        a.setAchievementId(5);
        a.setCode("FIRST");
        a.setName("First Step");
        a.setDescription("Unlocked first");

        when(achievementRepo.findUnlockedByUserId(eq(1))).thenReturn(List.of(a));

        mockMvc.perform(get("/users/1/achievements"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].achievementId").value(5))
            .andExpect(jsonPath("$[0].code").value("FIRST"))
            .andExpect(jsonPath("$[0].name").value("First Step"));
    }
}
