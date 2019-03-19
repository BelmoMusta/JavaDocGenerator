package musta.belmo.javacodecore;

import java.util.function.Function;
import java.util.function.Predicate;

public class When<T> {

    private final T t;
    private Predicate<T> ifPredicate;
    private Function<T, ?> thenAction;
    private Function<T, ?> elseAction;

    private When(T t) {
        this.t = t;
    }

    public static <S> When<S> of(S s) {
        return new When<>(s);
    }

    public When<T> ifCondtion(boolean predicate) {
        ifPredicate = t -> predicate;
        return this;
    }

    public When<T> ifCondtion(Predicate<T> predicate) {
        ifPredicate = predicate;
        return this;
    }

    public <R> When<T> thenReturn(Function<T, R> action) {
        thenAction = action;
        return this;
    }

    public <R> When<T> thenReturn(R value) {
        thenAction = t -> value;
        return this;
    }

    public <R> When<T> elseReturn(Function<T, R> action) {
        elseAction = action;
        return this;
    }

    public <R> When<T> elseReturn(R value) {
        elseAction = t -> value;
        return this;
    }

    @SuppressWarnings("all")
    public <R> R evaluate() {
        if (ifPredicate.test(t) && thenAction != null) {
            return (R) thenAction.apply(t);
        } else if (elseAction != null) {
            return (R) elseAction.apply(t);
        }
        return null;
    }

    public static void main(String[] args) {
        Object evaluate = When.of(10)
                .ifCondtion(false)
                .thenReturn(2)
                .elseReturn(0)
                .evaluate();
        System.out.println(evaluate);
    }
}
