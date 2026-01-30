package ch.wiss.m295.dto;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an achievement.
 */
public class AchievementUpdateRequest {

    @Size(max = 100)
    private String name;

    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
