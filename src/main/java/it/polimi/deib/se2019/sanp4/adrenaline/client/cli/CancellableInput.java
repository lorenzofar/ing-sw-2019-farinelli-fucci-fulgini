package it.polimi.deib.se2019.sanp4.adrenaline.client.cli;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.concurrent.*;

/**
 * A class extending a buffered reader that does not block the app
 * and allows for the input request to be cancelled
 */
public class CancellableInput extends BufferedReader {
    private final ExecutorService executor;
    /**
     * A future to perform reading methods
     */
    private Future future;

    CancellableInput(Reader in) {
        super(in);
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public String readLine() {
        future = executor.submit(super::readLine);
        try {
            return (String) future.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (CancellationException e) {
            return null;
        }
    }

    @Override
    public int read() {
        future = executor.submit((Callable<Integer>) super::read);
        try {
            return (int) future.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new CancellationException();
        } catch (CancellationException e) {
            throw new CancellationException();
        }
    }

    /**
     * Cancels the current input request
     */
    public void cancel() {
        future.cancel(true);
    }
}
