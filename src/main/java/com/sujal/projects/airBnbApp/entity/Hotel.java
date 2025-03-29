package com.sujal.projects.airBnbApp.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="hotel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String city;

    @Column(columnDefinition = "Text[]")
    private String[] photos;
    @Column(columnDefinition = "Text[]")
    private String[] amenities;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean active;

    @Embedded
    private HotelContactInfo contactInfo;

    @ManyToOne(optional = false,fetch = FetchType.LAZY) //TODO
    private User owner;

    @JsonIgnore
    @OneToMany(mappedBy = "hotel",fetch = FetchType.LAZY)
    private List<Room> rooms;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(getId(), hotel.getId()) && Objects.equals(getOwner(), hotel.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner());
    }
}
