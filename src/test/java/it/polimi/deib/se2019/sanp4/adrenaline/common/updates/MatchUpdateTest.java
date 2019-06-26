package it.polimi.deib.se2019.sanp4.adrenaline.common.updates;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.deib.se2019.sanp4.adrenaline.common.modelviews.MatchView;
import it.polimi.deib.se2019.sanp4.adrenaline.utils.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MatchUpdateTest {

    private ObjectMapper objectMapper = JSONUtils.getObjectMapper();
    private MatchView matchView;
    private List<String> killshots = Arrays.asList("player1", "player2", "player3");
    private boolean frenzy = true;

    @Test
    public void serialize_ShouldSucceed() throws IOException {
        matchView = new MatchView();

        MatchUpdate update = new MatchUpdate(matchView);
        String s = objectMapper.writeValueAsString(update);

        MatchUpdate matchUpdate = objectMapper.readValue(s, MatchUpdate.class);

        assertEquals(Collections.emptyList(), matchUpdate.getMatch().getKillshotsTrack());
        assertFalse(matchUpdate.getMatch().isFrenzy());

        update = new MatchUpdate(matchView);
        matchView.setFrenzy(frenzy);
        matchView.setKillshotsTrack(killshots);
        update.setMatch(matchView);

        s = objectMapper.writeValueAsString(update);

        matchUpdate = objectMapper.readValue(s, MatchUpdate.class);

        assertEquals(frenzy, matchUpdate.getMatch().isFrenzy());
        assertEquals(killshots, matchUpdate.getMatch().getKillshotsTrack());


    }
}