package mx.jhcue.testsdemo.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulerTest {

    private static final Instant I0 = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    private Scheduler scheduler;

    private Queue<Command> queue;


    @BeforeEach
    void setUp() {
        scheduler = new Scheduler();
        //noinspection unchecked
        queue = (Queue<Command>) ReflectionTestUtils.getField(scheduler, "queue");
    }

    @ParameterizedTest(name = "{index} \"{displayName}\" Arguments {argumentsWithNames}")
    @MethodSource("launchesCommandsInOrderAndAsRequiredArguments")
    @DisplayName("Launches Commands In Order And Required Arguments")
    void launchesCommandsInOrderAndAsRequired(List<List<String>> delaySpecsList, List<Integer> batches) {
        ReflectionTestUtils.setField(Command.class, "clock", Clock.fixed(I0, ZoneOffset.UTC));
        for (var delaySpecs : delaySpecsList) {
            var commands = delaySpecs.stream()
                    .map(delaySpec -> new ConcreteCommand(Duration.parse("PT" + delaySpec)))
                    .collect(Collectors.toList());
            scheduler.enqueue(commands);
        }
        Instant currentInstant = I0;
        do {
            var batch = Objects.requireNonNull((Collection<?>)ReflectionTestUtils.invokeMethod(scheduler, "runReadys"));
            assertEquals(batches.get(0), batch.size());

            batches = batches.stream().skip(1L).collect(Collectors.toList());
            currentInstant = currentInstant.plusSeconds(1L);
            ReflectionTestUtils.setField(Command.class, "clock", Clock.fixed(currentInstant, ZoneOffset.UTC));
        } while(! (queue.isEmpty() && batches.isEmpty()) );
    }

    static Stream<Arguments> launchesCommandsInOrderAndAsRequiredArguments() {
        return Stream.of(
            Arguments.of(List.of(List.of("0s", "5s")), List.of(1, 0, 0, 0, 0, 1)),
            Arguments.of(List.of(List.of("0s", "5s"), List.of("0s", "4s")), List.of(2, 0, 0, 0, 1, 1)),
            Arguments.of(List.of(List.of("0s", "5s"), List.of("0s", "4s"), List.of("4s", "5s")), List.of(2, 0, 0, 0, 2, 2))
        );
    }

    static class ConcreteCommand extends Command {

        public ConcreteCommand(Duration delay) {
            super(delay);
        }

        @Override
        public void run() {
            // Do nothing
        }
    }

}