package cz3002.g4.util;

public class Stopwatch { 

    private final long start;

    /**
     * Initializes a new stopwatch.
     */
    public Stopwatch() {
        start = System.currentTimeMillis();
    } 


    /**
     * Returns the elapsed CPU time (in seconds) since the stopwatch was created.
     *
     * @return elapsed CPU time (in seconds) since the stopwatch was created
     */
    public long elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000;
    }
} 
