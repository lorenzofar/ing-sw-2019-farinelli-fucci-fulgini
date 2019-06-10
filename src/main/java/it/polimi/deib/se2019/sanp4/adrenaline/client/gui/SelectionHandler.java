package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.ObservableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.client.gui.controls.SelectableOverlay;
import it.polimi.deib.se2019.sanp4.adrenaline.common.events.ChoiceResponse;
import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A class describing an object handling the selection of a certain object to reply to choice requests
 *
 * @param <T> The type of choice to retrieve
 */
public class SelectionHandler<T extends Serializable> implements Consumer<ObservableOverlay<T>> {

    /**
     * The client view to act on
     */
    private ClientView clientView;
    /**
     * The stage the request has been performed into, to close once it's completed
     */
    private Stage stage;

    private Collection<ObservableOverlay> observedPool;

    SelectionHandler(Collection<SelectableOverlay<T>> observedPool, Stage stage) {
        this.stage = stage;
        this.observedPool = new ArrayList<>(observedPool);
        observedPool.forEach(overlay -> {
            overlay.addListener(this);
            overlay.enable();
        });
    }

    SelectionHandler(Collection<SelectableOverlay<T>> observedPool) {
        this.observedPool = new ArrayList<>(observedPool);
        observedPool.forEach(overlay -> {
            overlay.addListener(this);
            overlay.enable();
        });
    }

    /**
     * Sets the client view to act upon
     *
     * @param clientView The object representing the client view
     */
    public void setClientView(ClientView clientView) {
        this.clientView = clientView;
    }

    /**
     * Ends the selection routine by notifying the view about completion and by closing the stage if needed
     */
    private void endSelection() {
        clientView.onRequestCompleted();
        // Try to close the stage if one has been provided
        // If no stage has been provided, it means it shouldn't be closed
        if (stage != null) {
            stage.close();
        }
    }

    @Override
    public void accept(ObservableOverlay<T> selectedOverlay) {
        observedPool.forEach(ObservableOverlay::reset);
        ChoiceRequest request = clientView.getCurrentRequest();
        if (request == null) {
            // We are trying to reply to a request that does not exist, hence we stop
            endSelection();
            return;
        }
        T choice = selectedOverlay.getData();
        ChoiceResponse<T> choiceResponse = new ChoiceResponse<>(clientView.getUsername(), clientView.getCurrentRequest().getUuid(), choice);
        clientView.notifyObservers(choiceResponse);
        endSelection();
    }

    @Override
    public Consumer<ObservableOverlay<T>> andThen(Consumer<? super ObservableOverlay<T>> consumer) {
        return (ObservableOverlay<T> t) -> {
            accept(t);
            consumer.accept(t);
        };
    }

    /**
     * Cancels the pending selection
     */
    public void cancel() {
        // Deregister from all the observed overlays
        observedPool.forEach(overlay -> {
            overlay.removeListener(this);
            overlay.reset();
        });
    }
}