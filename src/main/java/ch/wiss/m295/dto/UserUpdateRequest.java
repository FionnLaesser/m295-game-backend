package ch.wiss.m295.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a user.
 */
public class UserUpdateRequest {

    @Email
    @Size(max = 100)
    private String email;

    @Min(1)
    private Integer level;

    @Min(0)
    private Integer xp;

    @Min(0)
    private Integer coins;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getXp() { return xp; }
    public void setXp(Integer xp) { this.xp = xp; }

    public Integer getCoins() { return coins; }
    public void setCoins(Integer coins) { this.coins = coins; }
}
