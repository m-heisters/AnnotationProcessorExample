package com.mheisters.pizzafactory.pizzastore.domain.entity;

import com.mheisters.pizzafactory.annotations.Factory;

@Factory(
        id = "Tiramisu",
        type = Meal.class
)
public class Tiramisu implements Meal {

    @Override
    public float getPrice() {
        return 4.5f;
    }
}
