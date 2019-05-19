package it.polimi.deib.se2019.sanp4.adrenaline.controller.requests;

import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.DuplicateIdException;
import it.polimi.deib.se2019.sanp4.adrenaline.common.exceptions.UnknownIdException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class RequestManagerTest {

    /* A valid request that can be used by the test methods */
    private static StringRequest request;

    /* UUID of the above request */
    private static String uuid;

    /* Instance of a request manager (UUT) which is renewed before each test */
    private static RequestManager requestManager;

    private static StringRequest newRequest() {
        List<String> validChoices = Arrays.asList("a", "b", "c", "d");
        return new StringRequest("User message", validChoices, true);
    }

    @BeforeClass
    public static void classSetUp() {
        request = newRequest();
        uuid = request.getUuid();
    }

    @Before
    public void setUp() {
        /* Set up a new request manager before each test */
        requestManager = new RequestManager();
    }

    @Test
    public void insertRequest_newRequest_shouldSucceed() throws DuplicateIdException {
        /* Insert a new request in the empty manager */
        CompletableChoice choice = requestManager.insertRequest(request);

        /* Check that it returned the correct completable choice */
        assertNotNull(choice);
        assertEquals(request, choice.getRequest());
        /* Check the status */
        assertFalse(choice.isCompleted());
        assertFalse(choice.isCancelled());

        /* Check that the request has been inserted correctly */
        assertTrue(requestManager.isRequestPending(uuid));
        assertEquals(choice, requestManager.getPendingChoice(uuid));
    }

    @Test(expected = DuplicateIdException.class)
    public void insertRequest_duplicate_shouldThrow() throws DuplicateIdException {
        /* Insert a duplicate and check that it throws */
        requestManager.insertRequest(request);
        requestManager.insertRequest(request);
    }

    @Test
    public void completeRequest_uncompleted_validChoice_shouldSucceed()
            throws DuplicateIdException, UnknownIdException, InvalidChoiceException {
        /* Insert a new request */
        CompletableChoice choice = requestManager.insertRequest(request);

        /* Complete the choice */
        requestManager.completeRequest(uuid, "a");

        /* Check that the request has been removed */
        assertFalse(requestManager.isRequestPending(uuid));
    }

    @Test(expected = InvalidChoiceException.class)
    public void completeRequest_uncompleted_invalidChoice_shouldThrow()
            throws DuplicateIdException, UnknownIdException, InvalidChoiceException {
        /* Insert a new request */
        CompletableChoice choice = requestManager.insertRequest(request);
        /* Complete the choice */
        requestManager.completeRequest(uuid, "invalid");

        /* Check that the choice is actually not completed */
        assertFalse(choice.isCompleted());

        /* Check that the request is still pending */
        assertTrue(requestManager.isRequestPending(uuid));
    }

    @Test(expected = UnknownIdException.class)
    public void completeRequest_notInserted_shouldThrow() throws UnknownIdException, InvalidChoiceException {
        /* Insert a new request */
        requestManager.completeRequest(uuid, "some random dude");
    }

    @Test(expected = UnknownIdException.class)
    public void completeRequest_alreadyCompleted_shouldBeRemoved()
            throws DuplicateIdException, UnknownIdException, InvalidChoiceException {
        /* Insert a new request and complete it */
        requestManager.insertRequest(request);
        requestManager.completeRequest(uuid, "a"); /* Here the request is removed */

        /* Try to complete it again */
        requestManager.completeRequest(uuid, "b"); /* Should throw */
    }

    @Test
    public void cancelRequest_notCompleted_shouldCancel()
            throws DuplicateIdException {
        /* Insert a new request */
        CompletableChoice choice = requestManager.insertRequest(request);

        /* Cancel it, returns if it has been able to cancel */
        boolean success = requestManager.cancelRequest(uuid);
        assertTrue(success);

        /* Check that also the choice has been cancelled */
        assertTrue(choice.isCancelled());
    }

    @Test
    public void cancelRequest_alreadyCancelled_shouldFail() throws DuplicateIdException {
        /* Insert a new request and cancel it */
        CompletableChoice choice = requestManager.insertRequest(request);
        requestManager.cancelRequest(uuid); /* Cancelling goes fine */

        /* Now try to cancel it again and check that it failed */
        boolean success = requestManager.cancelRequest(uuid);
        assertFalse(success);
    }

    @Test
    public void cancelPendingRequests_pending_shouldBeCancelledAndRemoved() {
        /* Insert a few requests */
        Collection<CompletableChoice<String>> choices = IntStream
                /* Create 5 requests */
                .range(0, 5)
                .mapToObj(i -> newRequest())
                /* Insert them into the requestManager */
                .map(req -> {
                    try {
                        return requestManager.insertRequest(req);
                    } catch (DuplicateIdException e) {
                        fail(); /* This should never happen */
                        return null;
                    }
                })
                /* Create a collection to make sure that all insertions have been executed */
                .collect(Collectors.toList());
        /* Cancel all the requests */
        requestManager.cancelPendingRequests();

        /* Check cancellation for each choice */
        choices.forEach(c -> {
            /* Check that the request has been removed */
            assertFalse(requestManager.isRequestPending(c.getRequest().getUuid()));
            /* Check that the choice has been cancelled */
            assertTrue(c.isCancelled());
        });
    }
}