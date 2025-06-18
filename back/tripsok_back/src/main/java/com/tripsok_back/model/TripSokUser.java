package com.tripsok_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TripSokUser {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
