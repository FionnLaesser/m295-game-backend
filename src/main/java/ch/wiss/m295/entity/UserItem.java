package ch.wiss.m295.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
// Join-Entity zwischen User und Item.
// Speichert den aktuellen Besitz eines Users (kein History).

@Entity
@Table(name = "user_item")
public class UserItem {

    @EmbeddedId
    private UserItemId id;

    @Min(0)
    @Column(nullable = false)
    private int quantity = 0;

    public UserItemId getId() { return id; }
    public void setId(UserItemId id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
