package com.mheisters.pizzafactory.pizzastore.domain.service;

import com.mheisters.pizzafactory.pizzastore.domain.entity.*;

public class PizzaStore {
    MealFactory factory = new MealFactory();

    public Meal order(String mealName) {
        return factory.create(mealName);
    }

}
