package it.polimi.deib.se2019.sanp4.adrenaline.common.requests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An abstract class representing a request to choose between various alternatives, made to a player.
 * The player must then choose one of the alternatives (or even none of them, if {@link #isOptional()} returns
 * {@code true}.
 * The request also has an unique identifier, which will be used by its response to identify it.
 * @param <T> The type of objects representing the choices the player can make
 */
public abstract class ChoiceRequest<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 237085265707486604L;

    /** An unique identifier of the request */
    private String uuid;

    /** A human readable message shown to the recipient of the request */
    private String message;

    /** A list of objects representing the available choices */
    private List<T> choices;

    /** Indicates whether the request is optional */
    private boolean optional;

    /** Indicates the type of the choices */
    private Class<T> type;

    /**
     * Creates a new request
     * @param message The message associated to the request
     * @param choices The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param type the type of the choices
     * @param uuid unique identifier of the request, if not provided it will be auto-generated
     */
    public ChoiceRequest(String message, List<T> choices, boolean optional, Class<T> type, String uuid){
        if(message == null || choices == null){
            throw new NullPointerException("Found null parameters");
        }
        if(message.isEmpty()){
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (type == null) {
            throw new NullPointerException("The type of the choice cannot be null");
        }
        this.message = message;
        this.choices = choices;
        this.optional = optional;
        this.type = type;

        /* Generate uuid for this, if not provided */
        if (uuid == null || uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
        }
        this.uuid = uuid;
    }

    /**
     * Creates a new request, with auto-generated uuid
     * @param message The message associated to the request
     * @param choices The list of objects representing the available choices
     * @param optional {@code true} if the request is optional, {@code false} otherwise
     * @param type the type of the choices
     */
    public ChoiceRequest(String message, List<T> choices, boolean optional, Class<T> type) {
        this(message, choices, optional, type, UUID.randomUUID().toString());
    }

    /**
     * Checks whether the provided choice belong to the set of allowed choices
     * @param choice The object representing the choice, even null
     * @return {@code true} if the choice is valid, {@code false} otherwise
     */
    public boolean isChoiceValid(Object choice){
        /* Handle the case where no choice is provided */
        if (choice == null) return optional;

        /* Else check if the choice is between the provided alternatives */
        return choices.contains(choice);
    }

    /**
     * Retrieves the message associated to the request
     * @return The message associated to the request
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the available choices for the request
     * @return The list of objects representing the choices
     */
    public List<T> getChoices() {
        return new ArrayList<>(choices);
    }

    /**
     * Determines whether a request is optional
     * @return {@code true} if it is optional, {@code false} otherwise
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * Returns the type of the choices
     * @return type of the choices
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Returns the unique identifier of this request
     * @return the unique identifier of this request
     */
    public String getUuid() {
        return uuid;
    }
}
