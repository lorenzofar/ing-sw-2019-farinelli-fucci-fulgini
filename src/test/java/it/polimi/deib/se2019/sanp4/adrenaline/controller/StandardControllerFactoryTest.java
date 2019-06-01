package it.polimi.deib.se2019.sanp4.adrenaline.controller;

import it.polimi.deib.se2019.sanp4.adrenaline.model.match.Match;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class StandardControllerFactoryTest {

    @Mock
    private static Map<String, PersistentView> views;

    @Mock
    private static Match match;

    @Test(expected = NullPointerException.class)
    public void create_nullMatch_shouldThrow() {
        new StandardControllerFactory(null, views);
    }

    @Test(expected = NullPointerException.class)
    public void create_nullViews_shouldThrow() {
        new StandardControllerFactory(match, null);
    }

    @Test
    public void create_properParameters_shouldCreate() {
        StandardControllerFactory factory = new StandardControllerFactory(match, views);

        assertEquals(match, factory.getMatch());
        assertEquals(views, factory.getViews());
    }
}