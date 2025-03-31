package com.sujal.projects.airBnbApp.dto;

import com.sujal.projects.airBnbApp.entity.Hotel;
import com.sujal.projects.airBnbApp.entity.Room;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {

    private Long id;


    private LocalDate date;
    private Integer bookedCount;

    private Integer reservedCount;

    private Integer totalCount;

    private BigDecimal surgeFactor;
    private BigDecimal price;

    private String city;

    private Boolean closed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
