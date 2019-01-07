package musta.belmo.javacodecore;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MyOptional<T> {

    private static final MyOptional<?> EMPTY = new MyOptional<>();
    private final Optional<T> internalOptional;

    private MyOptional() {
        internalOptional = Optional.empty();
    }

    private MyOptional(Optional<T> other) {
        internalOptional = other;
    }

    private MyOptional(T t) {
        internalOptional = Optional.of(t);
    }

    public static <T> MyOptional<T> empty() {
        return (MyOptional<T>) EMPTY;
    }

    public static <T> MyOptional<T> of(T value) {
        return new MyOptional<>(value);
    }

    public static <T> MyOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public T get() {
        return internalOptional.get();
    }

    public boolean isPresent() {
        return internalOptional.isPresent();
    }

    public void ifPresent(Consumer<? super T> consumer) {
        internalOptional.ifPresent(consumer);
    }

    public MyOptional<T> filter(Predicate<? super T> predicate) {
        if (isPresent()) {
            return predicate.test(internalOptional.get()) ? this : empty();
        }
        return this;
    }

    public <U> MyOptional<U> map(Function<? super T, ? extends U> mapper) {
        if (isPresent()) {
            return empty();
        }
        return ofNullable(mapper.apply(internalOptional.orElse(null)));
    }

    public <U> MyOptional<U> flatMap(Function<? super T, MyOptional<U>> mapper) {

        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(internalOptional.orElse(null)));
        }

    }

    public T orElse(T other) {
        return internalOptional.orElse(other);
    }

    public T orElseGet(Supplier<? extends T> other) {
        return internalOptional.orElseGet(other);
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return internalOptional.orElseThrow(exceptionSupplier);
    }
    //

    public T orElseIfPredicate(T other, Predicate<T> predicate) {
        if (isPresent()) {
            final T t = get();
            if (!predicate.test(t)) {
                return t;
            }
        }
        return other;
    }

    public static <T> MyOptional<T> fromOptional(Optional<T> interfaceName) {
       return new MyOptional<>(interfaceName);
    }
}
