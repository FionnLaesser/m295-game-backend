package ch.wiss.m295.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enables @PreAuthorize / method security.
 * Required so role-based access (ADMIN/PLAYER) works as intended.
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
