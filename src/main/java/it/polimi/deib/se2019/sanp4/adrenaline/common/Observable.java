package it.polimi.deib.se2019.sanp4.adrenaline.common;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.events.Event;

import java.util.Collections;
import java.util.Set;

public abstract class Observable<T>{

    private Set<Observer<T>> observers;

    Observable(){
        observers = Collections.emptySet();
    }

    public void addObserver(Observer<T> observer){
        observers.add(observer);
    }

    public void removeObserver(Observer<T> observer){
        observers.remove(observer);
    }

    public void notifyObservers(Event event){
        observers.forEach(observer -> observer.update(this, event));
    }
}
