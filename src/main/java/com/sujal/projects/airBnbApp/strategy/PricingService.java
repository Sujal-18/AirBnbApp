package com.sujal.projects.airBnbApp.strategy;

import com.sujal.projects.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory){
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //apply additional strategies
        pricingStrategy  = new SurgePriceStrategy(pricingStrategy);
        pricingStrategy  = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy  = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);

    }
}
