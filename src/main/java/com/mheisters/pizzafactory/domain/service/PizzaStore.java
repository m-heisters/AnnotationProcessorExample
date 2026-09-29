package com.mheisters.pizzafactory.domain.service;

import com.mheisters.pizzafactory.domain.entity.*;

public class PizzaStore {
    MealFactory factory = new MealFactory();

    public Meal order(String mealName) {
        return factory.create(mealName);
    }

}
