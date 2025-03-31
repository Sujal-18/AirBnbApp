package com.sujal.projects.airBnbApp.dto;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelReportDTO {

    private Long bookingCount;
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenue;

}
