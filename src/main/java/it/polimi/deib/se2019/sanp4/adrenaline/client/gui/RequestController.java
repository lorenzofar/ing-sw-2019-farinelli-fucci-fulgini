package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;

import java.io.Serializable;

/**
 * An interface describing a controller used to perform requests
 *
 * @param <T> The type of the choice
 */
public interface RequestController<T extends Serializable> {

    /**
     * Prepares the window to perform the provided request
     * and populates the message and available choices
     *
     * @param request The object representing the request
     */
    void setup(ChoiceRequest<T> request);
}
