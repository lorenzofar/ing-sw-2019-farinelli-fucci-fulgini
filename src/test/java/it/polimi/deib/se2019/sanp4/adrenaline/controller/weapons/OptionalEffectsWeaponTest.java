package it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.EffectRequest;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.ControllerFactory;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.PersistentView;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.ChooseNoneAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.answerers.SendChoiceRequestAnswer;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.requests.CompletableChoice;
import it.polimi.deib.se2019.sanp4.adrenaline.controller.weapons.effects.AbstractEffect;
import it.polimi.deib.se2019.sanp4.adrenaline.model.ModelTestUtil;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.EffectDescription;
import it.polimi.deib.se2019.sanp4.adrenaline.model.items.weapons.WeaponCard;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchConfiguration;
import it.polimi.deib.se2019.sanp4.adrenaline.model.match.MatchCreator;
import it.polimi.deib.se2019.sanp4.adrenaline.view.MessageType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OptionalEffectsWeaponTest {

    private static Match match;
    private static Set<String> validNames;
    private static Map<String, PersistentView> views;
    private static PersistentView view;
    private static ControllerFactory factory;

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

    public AbstractEffect generateEffectController(EffectDescription description, boolean optional,
                                                   Set<String> dependsOnEffects, boolean completes)
            throws InterruptedException {
        AbstractEffect effect = mock(AbstractEffect.class);
        when(effect.getId()).thenReturn(description.getId());
        when(effect.isOptional()).thenReturn(optional);
        when(effect.getDependsOnEffects()).thenReturn(dependsOnEffects);
        when(effect.canBeUsed(anySet())).thenAnswer(invocationOnMock -> {
            Set<String> completed = invocationOnMock.getArgument(0);
            return completed.containsAll(dependsOnEffects) && !completed.contains(description.getId());
        });
        when(effect.use(view)).thenReturn(completes);
        return effect;
    }

    /* =================== ADD EFFECT =================== */

    @Test
    public void addEffect_baseEffect_onlyEffect_shouldSucceed() throws InterruptedException {
        /* Generate the base effect */
        EffectDescription ed = generateEffectDescription("base");
        AbstractEffect base = generateEffectController(ed, false, Collections.emptySet(), true);

        /* Set up a weapon card with that effect */
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Collections.singletonList(ed));

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);

        /* Check that it gets added correctly */
        weapon.addEffect(base);

        assertThat(weapon.getEffects().get("base"), is(base));
    }

    @Test
    public void addEffect_baseEffect_baseAlreadyAdded_shouldThrow() throws InterruptedException {
        /* Generate the base effect */
        EffectDescription ed = generateEffectDescription("base");
        AbstractEffect base = generateEffectController(ed, false, Collections.emptySet(), true);

        /* Set up a weapon card with that effect */
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Collections.singletonList(ed));

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);

        /* Add the base effect */
        weapon.addEffect(base);

        /* Try to add another base effect */
        EffectDescription ed2 = generateEffectDescription("another");
        AbstractEffect another = generateEffectController(ed2, false, Collections.emptySet(), true);

        try {
            weapon.addEffect(another);
            fail();
        } catch (IllegalArgumentException e) {
            /* Ok */
        }

        /* Check that the effect has not been added */
        assertThat(weapon.getEffects().get("base"), is(base));
        assertNull(weapon.getEffects().get("another"));
    }

    @Test
    public void addEffect_baseEffect_dependsOnEffects_shouldThrow() throws InterruptedException {
        /* Generate the base effect */
        EffectDescription ed = generateEffectDescription("base");
        AbstractEffect base =
                generateEffectController(ed, false, Collections.singleton("dependency"), true);

        /* Set up a weapon card with that effect */
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Collections.singletonList(ed));

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);

        /* Try to add the base effect */
        try {
            weapon.addEffect(base);
            fail();
        } catch (IllegalArgumentException e) {
            /* Ok */
        }

        /* Check that the effect has not been added */
        assertNull(weapon.getEffects().get("base"));
    }

    @Test
    public void addEffect_optionalEffect_baseExists_shouldBeAdded() throws InterruptedException {
        /* Generate the base effect */
        EffectDescription bd = generateEffectDescription("base");
        AbstractEffect base = generateEffectController(bd, false, Collections.emptySet(), true);

        /* Generate the optional effect */
        EffectDescription od = generateEffectDescription("optional");
        AbstractEffect optional = generateEffectController(od, true, Collections.emptySet(), true);
        optional.setDependsOnEffects(Collections.singleton("base"));

        /* Set up a weapon card with both effects */
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Arrays.asList(bd, od));

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);

        /* Add the effects */
        weapon.addEffect(base);
        weapon.addEffect(optional);

        /* Check that they've been added */
        assertThat(weapon.getEffects().get("base"), is(base));
        assertThat(weapon.getEffects().get("optional"), is(optional));
    }

    /* =================== USE =================== */

    @Test
    public void use_noEffects_shouldTerminate() throws InterruptedException {
        /* Set up a weapon card with no effects */
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Collections.emptyList());

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);

        /* Use it */
        weapon.use(view);

        /* Check no interaction with the user */
        verify(view, never()).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), any());
    }

    @Test
    public void use_baseEffectFails_shouldNotAllowNext() throws InterruptedException {
        /* Set up a weapon card with three effects */
        EffectDescription ed1 = generateEffectDescription("ed1");
        EffectDescription ed2 = generateEffectDescription("ed2");
        EffectDescription ed3 = generateEffectDescription("ed3");
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Arrays.asList(ed1,ed2,ed3));

        /* Set up the effect controllers */
        AbstractEffect base = /* Fails */
                generateEffectController(ed1, false, Collections.emptySet(), false);
        AbstractEffect optional1 = /* Not usable */
                generateEffectController(ed2, true, Collections.singleton("ed1"), true);
        AbstractEffect optional2 = /* Not usable */
                generateEffectController(ed3, true, Collections.singleton("ed1"), true);

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);
        weapon.addEffect(base);
        weapon.addEffect(optional1);
        weapon.addEffect(optional2);

        /* The base effect is automatically selected */
        weapon.use(view);

        /* Check that the user gets notified, but does not select the effect */
        verify(view, never()).sendChoiceRequest(any());
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the correct effects have been used */
        InOrder inOrder = inOrder(base, optional1, optional2);
        inOrder.verify(base).use(view);
        inOrder.verify(optional1, never()).use(view);
        inOrder.verify(optional2, never()).use(view);
    }

    @Test
    public void use_optionalHasNoDependencies_shouldAllowBeforeBase() throws InterruptedException {
        /* Set up a weapon card with three effects */
        EffectDescription ed1 = generateEffectDescription("ed1");
        EffectDescription ed2 = generateEffectDescription("ed2");
        EffectDescription ed3 = generateEffectDescription("ed3");
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Arrays.asList(ed1,ed2,ed3));

        /* Set up the effect controllers */
        AbstractEffect base = /* Fails */
                generateEffectController(ed1, false, Collections.emptySet(), true);
        AbstractEffect optional1 = /* Usable before base */
                generateEffectController(ed2, true, Collections.emptySet(), true);
        AbstractEffect optional2 = /* Usable after base */
                generateEffectController(ed3, true, Collections.singleton("ed1"), true);

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);
        weapon.addEffect(base);
        weapon.addEffect(optional1);
        weapon.addEffect(optional2);

        /* Set up user's answers */
        final List<EffectDescription> choices1 = new ArrayList<>();
        final List<EffectDescription> choices2 = new ArrayList<>();
        when(view.sendChoiceRequest(any(EffectRequest.class)))
                /* First request: choose the optional effect */
                .thenAnswer((SendChoiceRequestAnswer<EffectDescription>) req -> {
                    choices1.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(ed2);
                })
                /* Second request: choose not to use the second optional effect */
                .thenAnswer((SendChoiceRequestAnswer<EffectDescription>) req -> {
                    choices2.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(null);
                });

        /* Use the weapon */
        weapon.use(view);

        /* Check exactly two interactions and no warnings */
        verify(view, times(2)).sendChoiceRequest(any());
        verify(view, never()).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the choices in the requests were as expected */
        assertTrue(choices1.containsAll(Arrays.asList(ed1, ed2))); /* Before base */
        assertEquals(2, choices1.size());
        assertTrue(choices2.contains(ed3)); /* After base */
        assertEquals(1, choices2.size());

        /* Check that the correct effects have been used */
        InOrder inOrder = inOrder(base, optional1, optional2);
        inOrder.verify(optional1).use(view);
        inOrder.verify(base).use(view);
        inOrder.verify(optional2, never()).use(view);
    }

    @Test
    public void use_optionalFails_shouldAllowNext() throws InterruptedException {
        /* Set up a weapon card with three effects */
        EffectDescription ed1 = generateEffectDescription("ed1");
        EffectDescription ed2 = generateEffectDescription("ed2");
        EffectDescription ed3 = generateEffectDescription("ed3");
        WeaponCard weaponCard =
                new WeaponCard("weapon", "Weapon", Collections.emptyList(), Arrays.asList(ed1,ed2,ed3));

        /* Set up the effect controllers */
        AbstractEffect base = /* Fails */
                generateEffectController(ed1, false, Collections.emptySet(), true);
        AbstractEffect optional1 = /* Usable after base */
                generateEffectController(ed2, true, Collections.singleton("ed1"), false);
        AbstractEffect optional2 = /* Usable after base */
                generateEffectController(ed3, true, Collections.singleton("ed1"), true);

        /* Set up the weapon controller */
        OptionalEffectsWeapon weapon = new OptionalEffectsWeapon(weaponCard, match, factory);
        weapon.addEffect(base);
        weapon.addEffect(optional1);
        weapon.addEffect(optional2);

        /* Set up user's answers */
        final List<EffectDescription> choices1 = new ArrayList<>();
        final List<EffectDescription> choices2 = new ArrayList<>();
        when(view.sendChoiceRequest(any(EffectRequest.class)))
                /* First request: choose the optional effect which fails */
                .thenAnswer((SendChoiceRequestAnswer<EffectDescription>) req -> {
                    choices1.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(ed2);
                })
                /* Second request: choose the other optional effect */
                .thenAnswer((SendChoiceRequestAnswer<EffectDescription>) req -> {
                    choices2.addAll(req.getChoices());
                    return new CompletableChoice<>(req).complete(ed3);
                })
                /* Terminate */
                .thenAnswer(new ChooseNoneAnswer<>());

        /* Use the weapon */
        weapon.use(view);

        /* Check exactly three interactions and a warning */
        verify(view, times(3)).sendChoiceRequest(any());
        verify(view).showMessage(anyString(), eq(MessageType.WARNING));

        /* Check that the choices in the requests were as expected */
        assertTrue(choices1.containsAll(Arrays.asList(ed2, ed3))); /* Right after base */
        assertEquals(2, choices1.size());
        assertTrue(choices2.containsAll(Arrays.asList(ed2, ed3))); /* After ed2 failed */
        assertEquals(2, choices2.size());

        /* Check that the correct effects have been used */
        InOrder inOrder = inOrder(base, optional1, optional2);
        inOrder.verify(base).use(view);
        inOrder.verify(optional1).use(view);
        inOrder.verify(optional2).use(view);
    }
}