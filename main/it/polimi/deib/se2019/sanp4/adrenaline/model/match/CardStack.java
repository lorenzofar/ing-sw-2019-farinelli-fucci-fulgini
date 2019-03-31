package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

public interface CardStack<T>{
    public void discard(T card);
    T draw();
}