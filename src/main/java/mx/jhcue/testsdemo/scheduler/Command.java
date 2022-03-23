package mx.jhcue.testsdemo.scheduler;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class Command implements Delayed, Runnable {

    private static Clock clock = Clock.systemUTC();

    private final long runAtInMillis;

    public Command(Duration delay) {
        checkNotNull(delay, "Argument 'delay' is required");
        checkArgument(!delay.isNegative(), "Argument 'delay' must be non negative");
        checkState(clock != null, "Command's clock is undefined");
        this.runAtInMillis = clock.millis() + delay.toMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        checkState(clock != null, "Command's clock is undefined");
        return unit.convert(this.runAtInMillis - clock.millis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        checkArgument(other instanceof Command, "Argument 'other' must be a Command");
        return Long.compare(this.runAtInMillis, ((Command)other).runAtInMillis);
    }
}
