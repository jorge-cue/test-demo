package mx.jhcue.testsdemo.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.HAM;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.MUSHROOM;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.ONION;
import static mx.jhcue.testsdemo.model.AbstractPizza.Topping.PEPPER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CalzoneTest {

    @Test
    void canBakeACalzone() {
        Calzone calzone = new Calzone.Builder().sauceInside().addTopping(HAM).build();
        assertAll(
                () -> assertNotNull(calzone),
                () -> assertEquals(Set.of(HAM), calzone.getToppings())
        );
    }

    @Test
    void bakedCalzoneIsImmutable() {
        Calzone calzone = new Calzone.Builder().sauceInside().addTopping(HAM).sauceInside().build();

        Set<AbstractPizza.Topping> toppings = calzone.getToppings();
        toppings.add(ONION);

        // TODO: fix Calzone, because it is not immutable
        assertEquals(Set.of(HAM), calzone.getToppings());
    }

    @Test
    void backedCalzoneIsImmutableByBuilder() {
        Calzone.Builder builder = new Calzone.Builder();

        Calzone calzone = builder.addTopping(MUSHROOM).sauceInside().build();
        builder.addTopping(PEPPER);

        // TODO: fix Calzone, because it is not immutable
        assertEquals(Set.of(MUSHROOM), calzone.getToppings());
    }

}