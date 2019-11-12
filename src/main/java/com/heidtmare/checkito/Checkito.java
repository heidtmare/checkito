package com.heidtmare.checkito;

import java.util.function.Supplier;

/**
 *
 * @author cmheidt
 */
public class Checkito {

    @FunctionalInterface
    public interface Validation<T> {

        boolean validate(T input);
    }

    public static final <T> When<T> when(T input, Validation<T> val) {
        return new When<T>(input, val);
    }

    public static class When<T> {

        private final When previous;
        private final T input;
        private final Validation<T> val;

        public When(T input, Validation<T> val) {
            this(null, input, val);
        }

        public When(When previous, T input, Validation<T> val) {
            if (val == null) {
                throw new IllegalArgumentException("Validation must not be null!");
            }
            this.previous = previous;
            this.input = input;
            this.val = val;
        }

        public <T> When<T> andWhen(T input, Validation<T> val) {
            return new When<>(previous, input, val);
        }

        public void then(Runnable runnable) {
            if (evaluate()) {
                runnable.run();
            }
        }

        public <X extends Throwable> void thenThrow(Supplier<? extends X> exceptionSupplier) throws X {
            if (evaluate()) {
                throw exceptionSupplier.get();
            }
        }

        /**
         * Recursively validate. Will walk back up previous chain and eval from the root.
         *
         * @return
         */
        private boolean evaluate() {
            return previous == null
                    ? val.validate(input)
                    : previous.evaluate() && val.validate(input);
        }
    }

    public static class All<T> implements Validation<T> {

        private final Validation<T>[] vals;

        public All(Validation<T>... vals) {
            this.vals = vals;
        }

        @Override
        public boolean validate(T t) {
            for (Validation<T> val : vals) {
                if (!val.validate(t)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class None<T> implements Validation<T> {

        private final Validation[] vals;

        public None(Validation<T>... vals) {
            this.vals = vals;
        }

        @Override
        public boolean validate(T t) {
            for (Validation<T> val : vals) {
                if (val.validate(t)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class Not<T> implements Validation<T> {

        private final Validation<T> val;

        public Not(Validation<T> val) {
            this.val = val;
        }

        @Override
        public boolean validate(T t) {
            return !val.validate(t);
        }
    }

    public static <T> Validation<T> is(Validation<T> val) {
        return val;//passthrough
    }

    public static <T> Validation<T> is(Validation<T>... vals) {
        return new All<>(vals);
    }

    public static <T> Validation<T> not(Validation<T> val) {
        return new Not<>(val);
    }

    public static <T> Validation<T> not(Validation<T>... vals) {
        return new None<>(vals);
    }


    /*
    Object Validation
     */
    public static Validation Null() {
        return (value) -> value == null;
    }

    /*
    String Validation
     */
    public static <S extends String> Validation<S> empty() {
        return (S value) -> value == null || value.isEmpty();
    }

    public static <S extends String> Validation<S> blank() {
        return (S value) -> value == null || value.trim().isEmpty();
    }

    /*
    Number Validation
     */
    public static <N extends Number> Validation<N> greaterThan(N amount) {
        return (N value) -> value.doubleValue() > amount.doubleValue();
    }

    public static <N extends Number> Validation<N> greaterThanOrEqualTo(N amount) {
        return (N value) -> value.doubleValue() >= amount.doubleValue();
    }

    public static <N extends Number> Validation<N> lessThan(N amount) {
        return (N value) -> value.doubleValue() < amount.doubleValue();
    }

    public static <N extends Number> Validation<N> lessThanOrEqualTo(N amount) {
        return (N value) -> value.doubleValue() <= amount.doubleValue();
    }

}
