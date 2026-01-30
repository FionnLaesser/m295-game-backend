package ch.wiss.m295.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

// Demo-Authentifizierung mit In-Memory-Usern.
// Wird verwendet, um Rollen und Zugriffe ohne DB-Login zu testen.

@Configuration
public class InMemoryUsersConfig {
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin")) // Bei richtigen Projekt nicht hier gespeichert
                .roles("ADMIN")
                .build();

        UserDetails player = User.withUsername("player")
                .password(encoder.encode("player")) // Bei richtigen Projekt nicht hier gespeichert
                .roles("PLAYER")
                .build();

        return new InMemoryUserDetailsManager(admin, player);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
