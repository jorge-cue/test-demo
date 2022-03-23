package mx.jhcue.testsdemo.model;

public class Calzone extends AbstractPizza {
    private final boolean sauceInside;

    public static class Builder extends AbstractPizza.Builder<Builder> {
        private boolean sauceInside = false; // default

        public Builder sauceInside() {
            this.sauceInside = true;
            return this;
        }

        @Override
        Calzone build() {
            return new Calzone(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public Calzone(Builder builder) {
        super(builder);
        this.sauceInside = builder.sauceInside;
    }

    public boolean isSauceInside() {
        return sauceInside;
    }
}
