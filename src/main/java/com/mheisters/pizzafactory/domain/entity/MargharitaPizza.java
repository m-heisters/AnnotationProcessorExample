package com.mheisters.pizzafactory.domain.entity;

import com.mheisters.pizzafactory.annotation.processor.Factory;

@Factory(
        id = "Margharita",
        type = Meal.class
)
public class MargharitaPizza implements Meal {
    @Override
    public float getPrice() {
        return 6f;
    }
}
