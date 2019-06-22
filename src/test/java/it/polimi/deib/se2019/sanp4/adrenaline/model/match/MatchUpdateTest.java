package it.polimi.deib.se2019.sanp4.adrenaline.model.match;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.FullCapacityException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.observer.Observer;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.AddedWeaponUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.common.updates.ModelUpdate;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.board.SpawnSquare;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.player.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MatchUpdateTest {

    private static Match mockMatch;
    private static MatchConfiguration validConfig;
    private static Set<String> validNames;
    private static int skulls = 5;

    @Mock
    private static Observer<ModelUpdate> observer;

    @Mock
    private static Player mockplayer;

    @Mock
    private static AddedWeaponUpdate update;

    private static WeaponCard weaponCard;


    @Before
    public void setup() {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        validConfig = new MatchConfiguration(0, skulls);

        /* Create list of unique users */
        validNames = new HashSet<>();
        validNames.add("bzoto");
        validNames.add("slinky");
        validNames.add("zoniMyLord");

        /* Use match creator to create a mock match */
        mockMatch = MatchCreator.createMatch(validNames, validConfig);

        weaponCard = new WeaponCard("0", "weapon",
                new LinkedList<>(), new LinkedList<>());
    }

    @Test
    public void update_topObserverShouldReceive() throws FullCapacityException {
        SpawnSquare square;

        mockMatch.addObserver(observer);
        mockMatch.getBoard().addObserver(mockMatch);
        square = (SpawnSquare) mockMatch.getBoard().getSquare(2,0);
        square.addObserver(mockMatch.getBoard());
        square.refill(mockMatch);

        verify(observer).update(any(ModelUpdate.class));
    }

    @Test
    public void weaponCard_addObserver_ShouldSucceed() throws FullCapacityException {
        mockMatch.addObserver(observer);
        mockMatch.getPlayerByName("bzoto").addObserver(mockMatch);
        mockMatch.getPlayerByName("bzoto").addWeapon(weaponCard);

        verify(observer).update(any(AddedWeaponUpdate.class));
    }
}