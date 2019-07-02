package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatchConfigurationTest {

    @Test
    public void create_shouldSetProperties() {
        MatchConfiguration matchConfiguration = new MatchConfiguration(0, 2);

        assertEquals(0, matchConfiguration.getBoardId());
        assertEquals(2, matchConfiguration.getSkulls());
    }

    @Test
    public void setBoardId_stSkulls_shouldSetProperties() {
        MatchConfiguration matchConfiguration = new MatchConfiguration();
        matchConfiguration.setBoardId(1);
        matchConfiguration.setSkulls(5);

        assertEquals(1, matchConfiguration.getBoardId());
        assertEquals(5, matchConfiguration.getSkulls());
    }
}