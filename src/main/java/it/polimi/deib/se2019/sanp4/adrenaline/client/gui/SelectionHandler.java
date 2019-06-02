package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ObservableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A class describing an object handling the selection of a certain object to reply to choice requests
 *
 * @param <T> The type of choice to retrieve
 */
public class SelectionHandler<T extends Serializable> implements Consumer<ObservableOverlay> {

    /**
     * The client view to act on
     */
    private ClientView clientView;
    /**
     * Function to retrieve data from the selected overlay
     */
    private Function<ObservableOverlay, T> dataExtractor;

    SelectionHandler(ClientView clientView, Function<ObservableOverlay, T> dataExtractor) {
        this.clientView = clientView;
        this.dataExtractor = dataExtractor;
    }

    @Override
    public void accept(ObservableOverlay selectedOverlay) {
        ChoiceRequest request = clientView.getCurrentRequest();
        if (request == null) {
            // We are trying to reply to a request that does not exist, hence we stop
            return;
        }
        T choice = dataExtractor.apply(selectedOverlay);
        ChoiceResponse<T> choiceResponse = new ChoiceResponse<>(clientView.getUsername(), clientView.getCurrentRequest().getUuid(), choice);
        clientView.notifyObservers(choiceResponse);
        clientView.onRequestCompleted();
    }

    @Override
    public Consumer<ObservableOverlay> andThen(Consumer<? super ObservableOverlay> consumer) {
        return (ObservableOverlay t) -> {
            accept(t);
            consumer.accept(t);
        };
    }
}