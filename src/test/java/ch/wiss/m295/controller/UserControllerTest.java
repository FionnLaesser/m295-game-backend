package ch.wiss.m295.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ch.wiss.m295.entity.User;
import ch.wiss.m295.repository.UserRepository;

/**
 * US1 - Account erstellen
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import({
    ch.wiss.m295.config.SecurityConfig.class,
    ch.wiss.m295.config.MethodSecurityConfig.class
})
class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private UserRepository repo;

    @Test
    void createUser_success_201() throws Exception {
        when(repo.existsByUsername("u1")).thenReturn(false);
        when(repo.existsByEmail("u1@test.ch")).thenReturn(false);

        User saved = new User();
        saved.setUserId(1);
        saved.setUsername("u1");
        saved.setEmail("u1@test.ch");
        saved.setPasswordHash("HASH:test123");
        when(repo.save(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"u1\",\"email\":\"u1@test.ch\",\"password\":\"test123\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.username").value("u1"))
            .andExpect(jsonPath("$.email").value("u1@test.ch"));

        verify(repo).save(any(User.class));
    }

    @Test
    void createUser_duplicateUsername_409() throws Exception {
        when(repo.existsByUsername("u1")).thenReturn(true);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"u1\",\"email\":\"u1@test.ch\",\"password\":\"test123\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    void createUser_duplicateEmail_409() throws Exception {
        when(repo.existsByUsername("u1")).thenReturn(false);
        when(repo.existsByEmail("u1@test.ch")).thenReturn(true);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"u1\",\"email\":\"u1@test.ch\",\"password\":\"test123\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    void createUser_invalidData_400() throws Exception {
        // invalid email + blank username
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"\",\"email\":\"not-an-email\",\"password\":\"\"}"))
            .andExpect(status().isBadRequest());
    }
}
