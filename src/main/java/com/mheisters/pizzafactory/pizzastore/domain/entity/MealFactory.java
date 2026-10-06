package com.mheisters.pizzafactory.pizzastore.domain.entity;

public class MealFactory {

    public Meal create(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        if ("Margherita".equals(id)) {
            return new MargharitaPizza();
        }

        if ("Calzone".equals(id)) {
            return new CalzonePizza();
        }

        if ("Tiramisu".equals(id)) {
            return new Tiramisu();
        }

        throw new IllegalArgumentException("Unknown id for meal");
    }
}
