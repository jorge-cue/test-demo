package mx.jhcue.testsdemo.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.HAM;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.MUSHROOM;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.ONION;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.PEPPER;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.SAUSAGE;
import static mx.jhcue.testsdemo.model.Pizza.Size.LARGE;
import static mx.jhcue.testsdemo.model.Pizza.Size.MEDIUM;
import static mx.jhcue.testsdemo.model.Pizza.Size.SMALL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PizzaTest {

    @Test
    void canBakeAPizza() {
        Pizza pizza = new Pizza.Builder(MEDIUM).addTopping(HAM).build();
        assertAll(
                () -> assertNotNull(pizza),
                () -> assertEquals(MEDIUM, pizza.getSize()),
                () -> assertEquals(Set.of(HAM), pizza.getToppings())
        );
    }

    @Test
    void canBakeSeveralPizzasWithOneBuilder() {
        Pizza.Builder builder = new Pizza.Builder(LARGE);

        Pizza pizza1 = builder.addTopping(SAUSAGE).build();
        Pizza pizza2 = builder.addTopping(ONION).build();

        assertAll(
                () -> assertEquals(Set.of(SAUSAGE), pizza1.getToppings(), "pizza1 is not immutable"),
                () -> assertEquals(Set.of(SAUSAGE, ONION), pizza2.getToppings())
        );
    }

    @Test
    void bakedPizzaIsImmutable() {
        Pizza pizza = new Pizza.Builder(MEDIUM).addTopping(HAM).build();

        Set<AbstractPizza.Topping> toppings = pizza.getToppings();
        toppings.add(ONION);

        // TODO: fix Pizza, because it is not immutable
        assertEquals(Set.of(HAM), pizza.getToppings());
    }

    @Test
    void backedPizzaIsImmutableByBuilder() {
        Pizza.Builder builder = new Pizza.Builder(SMALL);

        Pizza pizza = builder.addTopping(MUSHROOM).build();
        builder.addTopping(PEPPER);

        // TODO: fix Pizza, because it is not immutable
        assertEquals(Set.of(MUSHROOM), pizza.getToppings());
    }

}