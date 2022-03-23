package mx.jhcue.testsdemo.model;

import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractPizza {

    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }

    private final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {

        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public final T addTopping(Topping topping) {
            toppings.add(topping);
            return self();
        }

        abstract AbstractPizza build();

        protected abstract T self();
    }

    protected AbstractPizza(Builder<?> builder) {
        toppings = builder.toppings;
    }

    public final Set<Topping> getToppings() {
        return toppings;
    }
}
