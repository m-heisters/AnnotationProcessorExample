package com.mheisters.pizzafactory.domain.entity;

import com.mheisters.pizzafactory.annotation.processor.Factory;

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
