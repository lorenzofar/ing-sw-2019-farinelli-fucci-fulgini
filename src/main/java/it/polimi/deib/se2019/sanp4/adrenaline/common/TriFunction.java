package it.polimi.deib.se2019.sanp4.adrenaline.common;

@FunctionalInterface
public interface TriFunction<T,U,S,R> {
    R apply(T t, U u, S s);
}
