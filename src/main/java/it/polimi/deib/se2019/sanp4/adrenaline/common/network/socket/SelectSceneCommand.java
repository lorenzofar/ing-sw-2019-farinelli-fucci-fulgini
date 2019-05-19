package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;
import it.polimi.deib.se2019.sanp4.adrenaline.view.ViewScene;

/**
 * Sent when the server selects a scene to be shown by the client view
 */
public class SelectSceneCommand implements SocketClientCommand {

    private ViewScene scene;

    /**
     * Creates a new command with the selected scene
     * @param scene the scene to be shown
     */
    @JsonCreator
    public SelectSceneCommand(@JsonProperty("scene") ViewScene scene) {
        if (scene == null) throw new NullPointerException("Scene cannot be null");
        this.scene = scene;
    }

    /**
     * Returns the scene
     * @return the scene
     */
    public ViewScene getScene() {
        return scene;
    }

    /**
     * Applies the command to given target, namely it is a {@code SocketServerConnection}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketClientCommandTarget target) {
        ClientView view = target.getClientView();

        /* Call the method on the view */
        view.selectScene(scene);
    }
}
