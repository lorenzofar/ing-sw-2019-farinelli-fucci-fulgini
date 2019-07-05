package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.Set;

/**
 * An abstract class representing an update coming from the model.
 * If recipients is null, the update is sent in broadcast.
 *
 * @author Tiziano Fucci
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "class"
)
public abstract class ModelUpdate implements Serializable {

    private static final long serialVersionUID = -1304721011350608639L;

    private Set<String> recipients;

    /**
     * Creates an update that will be sent in broadcast.
     */
    public ModelUpdate() {
        recipients = null;
    }

    /**
     * Creates an update that will be sent to a set of observers.
     *
     * @param recipients Who will receive the update
     */
    public ModelUpdate(Set<String> recipients) {
        this.recipients = recipients;
    }

    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }

    /**
     * Makes the provided visitor handle the update
     *
     * @param visitor The object representing the visitor
     */
    public abstract void accept(ModelUpdateVisitor visitor);
}
