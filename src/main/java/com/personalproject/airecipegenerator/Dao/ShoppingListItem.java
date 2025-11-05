package com.personalproject.airecipegenerator.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class ShoppingListItem {

    @Id
    @GeneratedValue
    private Long id;

    private String itemName; // e.g., "flour", "eggs"
    private boolean isChecked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public void setId(Long id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public ShoppingListItem() {}

    public ShoppingListItem(String itemName, User user) {
        this.itemName = itemName;
        this.user = user;
    }

    public Long getId() { return id; }
    public String getItemName() { return itemName; }
    public boolean isChecked() { return isChecked; }
    public User getUser() { return user; }

    public void setChecked(boolean checked) { isChecked = checked; }
}