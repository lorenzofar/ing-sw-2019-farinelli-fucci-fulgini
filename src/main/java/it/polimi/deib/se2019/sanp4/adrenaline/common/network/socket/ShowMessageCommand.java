package it.polimi.deib.se2019.sanp4.adrenaline.common.network.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;

/**
 * Sent to show a message on the client view
 */
public class ShowMessageCommand implements SocketClientCommand {

    private String text;

    private MessageType messageType;

    /**
     * Creates a new {@code ShowMessageCommand}
     * @param text the text you want to display
     * @param messageType the type of the message
     */
    @JsonCreator
    public ShowMessageCommand(
            @JsonProperty("text") String text,
            @JsonProperty("messageType") MessageType messageType
    ) {
        if (text == null) throw new NullPointerException("Text cannot be null");
        if (messageType == null) throw new NullPointerException("Message type cannot be null");
        this.text = text;
        this.messageType = messageType;
    }

    /**
     * Returns the text of the message
     * @return the text of the message
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the type of the message
     * @return the type of the message
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Applies the command to given target, namely it is a {@code SocketServerConnection}
     *
     * @param target target of the command
     */
    @Override
    public void applyOn(SocketClientCommandTarget target) {

    }
}
