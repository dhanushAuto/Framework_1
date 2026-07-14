package ai.util;

/**
 * Lightweight stopwatch used across the AI Sonar Auto-Fix pipeline to
 * measure and report execution time. Does not depend on any other
 * framework class so it is safe to use anywhere.
 */
public class ExecutionTimer {

    private long startNanos;
    private long stopNanos;
    private boolean running;

    public static ExecutionTimer start() {
        ExecutionTimer timer = new ExecutionTimer();
        timer.startNanos = System.nanoTime();
        timer.running = true;
        return timer;
    }

    public long stop() {
        this.stopNanos = System.nanoTime();
        this.running = false;
        return elapsedMillis();
    }

    public long elapsedMillis() {
        long end = running ? System.nanoTime() : stopNanos;
        return (end - startNanos) / 1_000_000;
    }

    public String elapsedFormatted() {
        long millis = elapsedMillis();
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long ms = millis % 1000;

        if (minutes > 0) {
            return String.format("%dm %ds %dms", minutes, seconds, ms);
        }
        return String.format("%ds %dms", seconds, ms);
    }
}
