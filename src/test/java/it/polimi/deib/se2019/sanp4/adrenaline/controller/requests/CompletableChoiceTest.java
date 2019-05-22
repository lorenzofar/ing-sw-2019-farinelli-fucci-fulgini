package it.polimi.deib.se2019.sanp4.adrenaline.controller.requests;

import it.polimi.deib.se2019.sanp4.adrenaline.common.requests.ChoiceRequest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/** Test subclass with String as the choice type */
class StringRequest extends ChoiceRequest<String> {
    StringRequest(String message, List<String> choices, boolean optional) {
        super(message, choices, optional, String.class);
    }
}

public class CompletableChoiceTest {

    private static StringRequest request;

    /* Executor to simulate async calls */
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    @BeforeClass
    public static void setUp() {
        List<String> validChoices = Arrays.asList("a", "b", "c", "d");
        request = new StringRequest("User message", validChoices, true);
    }

    @Test
    public void createChoice_requestProvided_shouldSucceed() {
        /* Should create the object in its initial state without throwing */
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);

        /* Check the initial state */
        assertFalse(completableChoice.isCompleted());
        assertFalse(completableChoice.isCancelled());
    }

    @Test(expected = NullPointerException.class)
    public void createChoice_nullRequestProvided_shouldThrow() {
        new CompletableChoice<>(null);
    }

    @Test
    public void complete_validChoice_shouldComplete()
            throws InvalidChoiceException, InterruptedException {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);

        /* Complete with a valid choice */
        completableChoice.complete("b");

        /* Check that the observers show the correct state */
        assertTrue(completableChoice.isCompleted());
        assertFalse(completableChoice.isCancelled());

        /* Check that the choice is available for retrieval */
        assertEquals("b", completableChoice.get());
    }

    @Test
    public void complete_nullValidChoice_shouldComplete()
            throws InvalidChoiceException, InterruptedException {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);

        /* Since the request is optional, the choice should complete also with a null value */
        completableChoice.complete(null);

        /* Check that the observers show the correct state */
        assertTrue(completableChoice.isCompleted());
        assertFalse(completableChoice.isCancelled());

        /* Check that the choice is available for retrieval */
        assertNull(completableChoice.get());
    }

    @Test(expected = InvalidChoiceException.class)
    public void complete_invalidChoice_shouldThrow()
            throws InvalidChoiceException {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);

        completableChoice.complete("invalid");

        /* Check the status */
        assertFalse(completableChoice.isCompleted());
        assertFalse(completableChoice.isCancelled());
    }

    @Test
    public void complete_alreadyCompleted_shouldHaveNoEffect() throws InvalidChoiceException, InterruptedException {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);
        completableChoice.complete("a");

        /* Try to complete it again */
        completableChoice.complete("b");
        assertEquals("a", completableChoice.get());
    }

    @Test
    public void cancel_uncompleted_shouldReturnTrue() {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);
        assertTrue(completableChoice.cancel());

        /* Check the status */
        assertTrue(completableChoice.isCompleted());
        assertTrue(completableChoice.isCancelled());
    }

    @Test
    public void cancel_alreadyCompleted_shouldReturnFalse() throws InvalidChoiceException {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);
        completableChoice.complete("a"); /* Complete the choice */

        /* Now try to cancel it */
        assertFalse(completableChoice.cancel());
        /* Check that it has not been cancelled */
        assertTrue(completableChoice.isCompleted());
        assertFalse(completableChoice.isCancelled());
    }

    @Test
    public void get_asyncCompletion_shouldReturn() throws Exception {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);

        /* Block on the completable choice and return the result as a future */
        Future returnedValue = executor.submit(() -> {
            try {
                return completableChoice.get();
            } catch (InterruptedException e) {
                return null;
            }
        });

        /* Complete the choice */
        completableChoice.complete("a");
        /* Check the result of the future */
        assertEquals("a", returnedValue.get());
    }

    @Test
    public void get_asyncCancellation_shouldThrow() throws ExecutionException, InterruptedException {
        CompletableChoice<String> completableChoice = new CompletableChoice<>(request);

        /* Block on the completable choice and check that it throws */
        Future successful = executor.submit(() -> {
            try {
                completableChoice.get();
                return false; /* Should throw before this */
            } catch (InterruptedException e) {
                return false; /* No interruption should occur */
            } catch (CancellationException e) {
                return true; /* Should get this exception */
            }
        });

        /* Cancel the choice */
        completableChoice.cancel();
        /* Assert that the exception has been thrown */
        assertTrue((Boolean) successful.get());
    }
}