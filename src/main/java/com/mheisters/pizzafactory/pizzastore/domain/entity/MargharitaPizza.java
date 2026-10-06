package com.mheisters.pizzafactory.pizzastore.domain.entity;

import com.mheisters.pizzafactory.annotations.Factory;

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
