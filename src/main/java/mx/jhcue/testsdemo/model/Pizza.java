package mx.jhcue.testsdemo.model;

import static com.google.common.base.Preconditions.checkNotNull;

public class Pizza extends AbstractPizza {

    public enum Size { SMALL, MEDIUM, LARGE }

    private final Size size;

    public static class Builder extends AbstractPizza.Builder<Builder> {
        private final Size size;

        public Builder(Size size) {
            checkNotNull(size);
            this.size = size;
        }

        @Override
        public Pizza build() {
            return new Pizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public Pizza(Builder builder) {
        super(builder);
        this.size = builder.size;
    }

    public Size getSize() {
        return size;
    }
}
