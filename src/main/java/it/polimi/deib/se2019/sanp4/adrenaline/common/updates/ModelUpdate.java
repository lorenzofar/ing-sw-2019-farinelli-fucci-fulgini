package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

/**
 * An abstract class representing an update coming from the model.
 * If recipients is null, the update is sent in broadcast.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "class"
)
public abstract class ModelUpdate {

    private Set<String> recipients;

    /**
     * Creates an update that will be sent in broadcast.
     */
    public ModelUpdate(){
        recipients = null;
    }

    /**
     * Creates an update that will be sent to a set of observers.
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
}
