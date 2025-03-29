package com.sujal.projects.airBnbApp.entity;

import jakarta.persistence.*;

@Entity
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
