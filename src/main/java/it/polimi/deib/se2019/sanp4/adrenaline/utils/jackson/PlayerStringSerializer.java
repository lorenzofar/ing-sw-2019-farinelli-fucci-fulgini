package it.polimi.deib.se2019.sanp4.adrenaline.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;

import java.io.IOException;

/**
 * Used to serialize a Player as its name (e.g. key of a map)
 */
public class PlayerStringSerializer extends StdSerializer<Player> {

    protected PlayerStringSerializer() {
        super(Player.class);
    }

    @Override
    public void serialize(Player player, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeFieldName(player.getName());
    }
}
