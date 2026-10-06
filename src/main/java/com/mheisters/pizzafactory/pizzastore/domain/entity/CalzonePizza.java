package com.mheisters.pizzafactory.pizzastore.domain.entity;

import com.mheisters.pizzafactory.annotations.Factory;

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
