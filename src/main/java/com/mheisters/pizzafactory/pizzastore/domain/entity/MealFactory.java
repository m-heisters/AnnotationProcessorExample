package com.mheisters.pizzafactory.pizzastore.domain.entity;

public class MealFactory {

    public Meal create(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        return switch (id) {
            case "Margherita" -> new MargharitaPizza();
            case "Calzone" -> new CalzonePizza();
            case "Tiramisu" -> new Tiramisu();
            default -> throw new IllegalArgumentException("Unknown id for meal");
        };

    }
}
