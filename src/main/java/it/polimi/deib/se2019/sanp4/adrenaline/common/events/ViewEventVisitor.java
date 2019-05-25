package it.polimi.deib.se2019.sanp4.adrenaline.common.events;

import java.io.Serializable;

public interface ViewEventVisitor {

    /**
     * Handles given choice response
     * @param choiceResponse the choice response event
     * @param <T> the type of the choice
     */
    <T extends Serializable> void visit(ChoiceResponse<T> choiceResponse);
}
