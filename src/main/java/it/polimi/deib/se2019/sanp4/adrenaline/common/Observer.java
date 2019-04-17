package it.polimi.deib.se2019.sanp4.adrenaline.common;

import it.polimi.deib.se2019.sanp4.adrenaline.controller.events.Event;

public interface Observer<T> {
    void update(Observable<T> observable, Event event);
}
