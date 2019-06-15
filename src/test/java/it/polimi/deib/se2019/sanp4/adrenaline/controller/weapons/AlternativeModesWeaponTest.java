package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.EffectRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.AbstractEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(MockitoJUnitRunner.class)
public class AlternativeModesWeaponTest {

    private static Match match;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;
    private static ControllerFactory factory;

    @Captor
    private static ArgumentCaptor<EffectRequest> effectRequestCaptor;

    @BeforeClass
    public static void classSetup() {
        /* Disable logging */
        ModelTestUtil.disableLogging();

        /* Load schemas and assets */
        ModelTestUtil.loadCreatorResources();

        MatchConfiguration validConfig = new MatchConfiguration(0, 5);

        /* Create list of unique users */
        validNames = new HashSet<>();
        validNames.add("bzoto");
        validNames.add("loSqualo");
        validNames.add("zoniMyLord");


        /* Create a match */
        match = MatchCreator.createMatch(validNames, validConfig);
    }

    @Before
    public void setUp() {
        /* Create views of players which respond with their names */
        views = new HashMap<>();
        validNames.forEach(n -> {
            PersistentView v = mock(PersistentView.class);
            /* Don't need to respond with name */
            views.put(n,v);
        });
        view = views.get("bzoto");

        /* Create a stub of the controller factory (only stub relevant methods) */
        factory = mock(ControllerFactory.class);
    }

    public EffectDescription generateEffectDescription(String id) {
        return new EffectDescription(id, "name", "desc", Collections.emptyList());
    }

    public AbstractEffect generateEffectController(EffectDescription description) {
        AbstractEffect effect = mock(AbstractEffect.class);
        when(effect.getId()).thenReturn(description.getId());
        return effect;
    }

    /* ====================== ADD MODES =============================== */

    @Test
    public void addEffect_shouldAddAll() {
        /* Generate two distinct effects */
        EffectDescription ed1 = generateEffectDescription("e1");
        EffectDescription ed2 = generateEffectDescription("e2");
        AbstractEffect e1 = generateEffectController(ed1);
        AbstractEffect e2 = generateEffectController(ed2);

        /* Set up a weapon card with those effects */
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Arrays.asList(ed1, ed2));

        /* Create a weapon */
        AlternativeModesWeapon weapon = new AlternativeModesWeapon(weaponCard, match, factory);

        /* Add the two effects */
        weapon.addEffect(e1);
        weapon.addEffect(e2);

        /* Check that the two effects have been added */
        assertThat(weapon.getEffects().get("e1"), is(e1));
        assertThat(weapon.getEffects().get("e2"), is(e2));

    }

    /* ====================== USE MODE =============================== */

    @Test
    public void use_noEffects_shouldTerminate() throws InterruptedException {
        /* Set up a weapon card with no modes */
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Collections.emptyList());

        /* Set up a controller for that weapon, also with no modes */
        AlternativeModesWeapon weapon = new AlternativeModesWeapon(weaponCard, match, factory);

        /* Use it */
        weapon.use(view);

        /* Check no interaction with the user */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());
    }

    @Test
    public void use_oneMode_shouldNotAskPlayer() throws InterruptedException {
        /* Set up a weapon card with only one mode */
        EffectDescription ed1 = generateEffectDescription("e1");
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Collections.singletonList(ed1));

        /* Set up a controller for that weapon, with the respective mode */
        AlternativeModesWeapon weapon = new AlternativeModesWeapon(weaponCard, match, factory);
        AbstractEffect e1 = generateEffectController(ed1);
        weapon.addEffect(e1);

        /* Use it */
        weapon.use(view);

        /* Check no interaction with the user */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());

        /* Check that effect has been used */
        verify(e1).use(view);
    }

    @Test
    public void use_multipleModes_shouldAskPlayer() throws InterruptedException {
        /* Set up a weapon card with only one mode */
        EffectDescription ed1 = generateEffectDescription("e1");
        EffectDescription ed2 = generateEffectDescription("e2");
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Arrays.asList(ed1, ed2));

        /* Set up a controller for that weapon, with the respective mode */
        AlternativeModesWeapon weapon = new AlternativeModesWeapon(weaponCard, match, factory);
        AbstractEffect e1 = generateEffectController(ed1);
        AbstractEffect e2 = generateEffectController(ed2);
        weapon.addEffect(e1);
        weapon.addEffect(e2);

        /* Set up user's answer: choose the second mode */
        when(view.sendChoiceRequest(effectRequestCaptor.capture()))
                .thenAnswer((SendChoiceRequestAnswer<EffectDescription>) req ->
                        new CompletableChoice<>(req).complete(req.getChoices().get(1))
                );

        /* Use it */
        weapon.use(view);

        /* Check that the list of choices was correct */
        List<EffectDescription> givenChoices = effectRequestCaptor.getValue().getChoices();
        assertTrue(givenChoices.stream().anyMatch(ed -> ed.getId().equals("e1")));
        assertTrue(givenChoices.stream().anyMatch(ed -> ed.getId().equals("e2")));
        assertEquals(2, givenChoices.size());

        /* Check single interaction with the user */
        verify(view).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());

        /* Check that only the second effect has been used */
        verify(e1, never()).use(view);
        verify(e2).use(view);
    }
}