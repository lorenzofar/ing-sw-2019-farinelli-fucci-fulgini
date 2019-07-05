package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import java.io.Serializable;

/**
 * Visitor for events coming from the view.
 * <p>
 * Classes implementing this interface can handle each type of event with a specific method.
 * </p>
 *
 * @author Alessandro Fulgini
 */
public interface ViewEventVisitor {

    /**
     * Handles given choice response
     *
     * @param choiceResponse the choice response event
     * @param <T>            the type of the choice
     */
    <T extends Serializable> void visit(ChoiceResponse<T> choiceResponse);
}
