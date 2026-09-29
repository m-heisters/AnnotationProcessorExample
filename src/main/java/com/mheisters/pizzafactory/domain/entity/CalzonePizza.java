package com.mheisters.pizzafactory.domain.entity;

import com.mheisters.pizzafactory.annotation.processor.Factory;

@Factory(
        id = "Calzone",
        type = Meal.class
)
public class CalzonePizza implements Meal {

    @Override
    public float getPrice() {
        return 8.5f;
    }
}
