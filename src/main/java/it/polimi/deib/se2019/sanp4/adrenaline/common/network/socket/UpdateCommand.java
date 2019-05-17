package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;

/**
 * Sent to deliver an update by an observed object to the client's view
 */
public class UpdateCommand implements SocketClientCommand {

    private ModelUpdate update;

    /**
     * Create new command
     * @param update the update which has to be sent to the client
     */
    @JsonCreator
    public UpdateCommand(@JsonProperty("update") ModelUpdate update) {
        if (update == null) throw new NullPointerException("Update cannot be null");
        this.update = update;
    }

    /**
     * Returns the update
     * @return the update
     */
    public ModelUpdate getUpdate() {
        return update;
    }

    /**
     * Applies the command to given target, namely it is a {@code SocketServerConnection}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketClientCommandTarget target) {
        /* TODO: Apply this on the client */
    }
}
