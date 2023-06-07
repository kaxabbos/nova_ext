package com.nova.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Bonuses {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Long userId;
    private String admin;
    private String date;
    private int bonus;

    public Bonuses(Long userId, String admin, String date, int bonus) {
        this.userId = userId;
        this.admin = admin;
        this.date = date;
        this.bonus = bonus;
    }
}
