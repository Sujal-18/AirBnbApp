package com.sujal.projects.airBnbApp.strategy;

import com.sujal.projects.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;


import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePriceStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price =wrapped.calculatePrice(inventory);
        return price.multiply( inventory.getSurgeFactor()); //wrapped.calculatePrice(inventory) will not call itself recursively unless wrapped is an instance of SurgePriceStrategy itself (which would create an infinite loop). However, for this to work correctly, wrapped must be another implementation of PricingStrategy that calculates the base price before applying the surge factor.
    }
}
