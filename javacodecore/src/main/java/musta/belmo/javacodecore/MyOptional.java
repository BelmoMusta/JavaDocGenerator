package musta.belmo.javacodecore;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * TODO: Compléter la description de cette classe
 *
 * @author mbelmokhtar
 * @since 1.4.0.SNAPSHOT
 * @version 7.1
 */
public class MyOptional<T> {

    /**
     * La constante {@link #EMPTY} de type {@link MyOptional<?>} ayant la valeur new MyOptional<>().
     */
    private static final MyOptional<?> EMPTY = new MyOptional<>();

    /**
     * L'attribut {@link #internalOptional}.
     */
    private final Optional<T> internalOptional;

    /**
     * Constructeur de la classe MyOptional
     */
    private MyOptional() {
        internalOptional = Optional.empty();
    }

    /**
     * Constructeur de la classe MyOptional
     *
     * @param other{@link Optional}
     */
    private MyOptional(Optional<T> other) {
        internalOptional = other;
    }

    /**
     * Constructeur de la classe MyOptional
     *
     * @param t{@link T}
     */
    private MyOptional(T t) {
        internalOptional = Optional.of(t);
    }

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @return MyOptional
     */
    public static <T> MyOptional<T> empty() {
        return (MyOptional<T>) EMPTY;
    }

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param value {@link T}
     * @return MyOptional
     */
    public static <T> MyOptional<T> of(T value) {
        return new MyOptional<>(value);
    }

    /**
     * Of nullable
     *
     * @param value {@link T}
     * @return MyOptional
     */
    public static <T> MyOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * @return Attribut {@link #}
     */
    public T get() {
        return internalOptional.get();
    }

    /**
     * @return Attribut {@link #present}
     */
    public boolean isPresent() {
        return internalOptional.isPresent();
    }

    /**
     * If present
     *
     * @param consumer {@link Consumer<? super T>}
     */
    public void ifPresent(Consumer<? super T> consumer) {
        internalOptional.ifPresent(consumer);
    }

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param predicate {@link Predicate<? super T>}
     * @return MyOptional
     */
    public MyOptional<T> filter(Predicate<? super T> predicate) {
        if (isPresent()) {
            return predicate.test(internalOptional.get()) ? this : empty();
        }
        return this;
    }

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param mapper {@link Function<? super T,? extends U>}
     * @return MyOptional
     */
    public <U> MyOptional<U> map(Function<? super T, ? extends U> mapper) {
        if (isPresent()) {
            return empty();
        }
        return ofNullable(mapper.apply(internalOptional.orElse(null)));
    }

    /**
     * Flat map
     *
     * @param mapper {@link Function<? super T,MyOptional>}
     * @return MyOptional
     */
    public <U> MyOptional<U> flatMap(Function<? super T, MyOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(internalOptional.orElse(null)));
        }
    }

    /**
     * Or else
     *
     * @param other {@link T}
     * @return T
     */
    public T orElse(T other) {
        return internalOptional.orElse(other);
    }

    /**
     * Or else get
     *
     * @param other {@link Supplier<? extends T>}
     * @return T
     */
    public T orElseGet(Supplier<? extends T> other) {
        return internalOptional.orElseGet(other);
    }

    /**
     * Or else throw
     *
     * @param exceptionSupplier {@link Supplier<? extends X>}
     * @return T
     * @throws X Exception levée si erreur.
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return internalOptional.orElseThrow(exceptionSupplier);
    }

    // 
    /**
     * Or else if predicate
     *
     * @param other {@link T}
     * @param predicate {@link Predicate}
     * @return T
     */
    public T orElseIfPredicate(T other, Predicate<T> predicate) {
        if (isPresent()) {
            final T t = get();
            if (!predicate.test(t)) {
                return t;
            }
        }
        return other;
    }

    /**
     * From optional
     *
     * @param interfaceName {@link Optional}
     * @return MyOptional
     */
    public static <T> MyOptional<T> fromOptional(Optional<T> interfaceName) {
        return new MyOptional<>(interfaceName);
    }
}
