package mx.jhcue.testsdemo.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.HAM;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.ONION;
import static mx.jhcue.testsdemo.model.Pizza.Size.MEDIUM;
import static org.junit.jupiter.api.Assertions.*;

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
    void bakedPizzaIsImmutable() {
        Pizza pizza = new Pizza.Builder(MEDIUM).addTopping(HAM).build();

        Set<AbstractPizza.Topping> toppings = pizza.getToppings();
        toppings.add(ONION);

        // TODO: fix Pizza, because it is not immutable
        assertEquals(Set.of(HAM), pizza.getToppings());
    }

}