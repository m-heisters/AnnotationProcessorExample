package com.mheisters.pizzafactory;


import com.mheisters.pizzafactory.domain.entity.Meal;
import com.mheisters.pizzafactory.domain.service.PizzaStore;

public class Main {
    static void main() {

        PizzaStore pizzaStore = new PizzaStore();
        Meal meal = pizzaStore.order("Calzone");
        System.out.println("Bill: $" + meal.getPrice());
    }
}
