package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.MatchView;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MatchUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private MatchView matchView;
    private int killshots = 4;
    private boolean frenzy = true;

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        matchView = new MatchView();

        MatchUpdate update = new MatchUpdate(matchView);
        String s = objectMapper.writeValueAsString(update);

        MatchUpdate matchUpdate = objectMapper.readValue(s, MatchUpdate.class);

        assertEquals(0, matchUpdate.getMatch().getKillshotsCount());
        assertFalse(matchUpdate.getMatch().isFrenzy());

        update = new MatchUpdate(matchView);
        matchView.setFrenzy(frenzy);
        matchView.setKillshotsCount(killshots);
        update.setMatch(matchView);

        s = objectMapper.writeValueAsString(update);

        matchUpdate = objectMapper.readValue(s, MatchUpdate.class);

        assertEquals(frenzy, matchUpdate.getMatch().isFrenzy());
        assertEquals(killshots, matchUpdate.getMatch().getKillshotsCount());


    }
}