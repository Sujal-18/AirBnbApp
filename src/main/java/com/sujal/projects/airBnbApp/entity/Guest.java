package com.sujal.projects.airBnbApp.entity;

import com.sujal.projects.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.events.Event;

@Entity
@Getter
@Setter
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;


    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

}
