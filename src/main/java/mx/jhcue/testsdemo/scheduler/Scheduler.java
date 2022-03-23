package mx.jhcue.testsdemo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public final class Scheduler {

    private final DelayQueue<Command> queue = new DelayQueue<>();

    private final Executor executor = Executors.newWorkStealingPool();

    public void enqueue(Collection<? extends Command> commands) {
        checkNotNull(commands, "Argument 'commands' is required");
        queue.addAll(commands);
    }

    private Collection<CompletableFuture<Void>> runReadys() {
        var ready = new ArrayList<Command>();
        queue.drainTo(ready);
        return ready.stream().map(c -> CompletableFuture.runAsync(c, executor)).collect(Collectors.toList());
    }

    @Scheduled(fixedDelayString = "${scheduler.delay:10s}", fixedRateString = "${scheduler.rate:1s}")
    public void execute() {
        runReadys();
    }
}
