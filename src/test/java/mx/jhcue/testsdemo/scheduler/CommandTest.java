package mx.jhcue.testsdemo.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandTest {

    private static final Instant I0 = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    void delayArgumentIsRequired() {
        var exception = assertThrows(NullPointerException.class, () -> new ConcreteCommand(null));
        assertEquals("Argument 'delay' is required", exception.getMessage());
    }

    @Test
    void delayArgumentMustBeNonNegative() {
        var exception = assertThrows(IllegalArgumentException.class, () -> new ConcreteCommand(Duration.parse("PT-1s")));
        assertEquals("Argument 'delay' must be non negative", exception.getMessage());
    }

    @Test
    void commandsClockMustBeDefinedWhenCreatingACommand() {
        ReflectionTestUtils.setField(Command.class, "clock", null);
        var exception = assertThrows(IllegalStateException.class, () -> new ConcreteCommand(Duration.ZERO));
        assertEquals("Command's clock is undefined", exception.getMessage());
    }

    @Test
    void commandsClockMustBeDefinedWhenComputingDelay() {
        var clock = Clock.fixed(I0, ZoneOffset.UTC); // Create clock fixed at I0
        ReflectionTestUtils.setField(Command.class, "clock", clock); // Set overall Command's clock to I0
        var command = new ConcreteCommand(Duration.ZERO);

        ReflectionTestUtils.setField(Command.class, "clock", null);
        var exception = assertThrows(IllegalStateException.class, () -> command.getDelay(TimeUnit.MILLISECONDS));
        assertEquals("Command's clock is undefined", exception.getMessage());
    }

    @Test
    void argumentOtherMustBeACommandInCompareTo() {
        var clock = Clock.fixed(I0, ZoneOffset.UTC); // Create clock fixed at I0
        ReflectionTestUtils.setField(Command.class, "clock", clock); // Set overall Command's clock to I0
        var command1 = new ConcreteCommand(Duration.ZERO);
        var other = new Delayed() {
            @Override
            public long getDelay(TimeUnit unit) {
                return 0;
            }

            @Override
            public int compareTo(Delayed o) {
                return 0;
            }
        };
        var exception = assertThrows(IllegalArgumentException.class, () -> command1.compareTo(other));
        assertEquals("Argument 'other' must be a Command", exception.getMessage());
    }

    @ParameterizedTest(name = "{index} \"{displayName}\" Arguments {argumentsWithNames}")
    @ValueSource(strings = {"0s", "5s", "1m"})
    @DisplayName("Delay decays as clock moves forward")
    void delayDecaysAsClockMovesForward(String delaySpec) {
        var clock = Clock.fixed(I0, ZoneOffset.UTC); // Create clock fixed at I0
        ReflectionTestUtils.setField(Command.class, "clock", clock); // Set overall Command's clock to I0
        var delay = Duration.parse("PT" + delaySpec); // Compute specified delay
        var command = new ConcreteCommand(delay); // Create command to be run after the specified delay
        var expectedDelay = delay.toSeconds(); // Get expectedDelay in seconds
        do {
            var actualDelay = command.getDelay(TimeUnit.SECONDS); // Get actual delay from command
            assertEquals(expectedDelay, actualDelay); // Validate expected vs actual
            // tic, toc, ...
            clock = Clock.fixed(clock.instant().plusSeconds(1L), clock.getZone()); // move clock 1 second forward
            ReflectionTestUtils.setField(Command.class, "clock", clock); // set overall Command's clock
        } while (--expectedDelay >= 0); // expected delay is decreased by one second, if it becomes negative we are done!
    }

    @ParameterizedTest(name = "{index} \"{displayName}\" Arguments {argumentsWithNames}")
    @MethodSource("mustBeOrderedAccordingToCreationTimePlusRequestedDelayArguments")
    @DisplayName("Must be ordered according to creation time plus requested delay")
    void mustBeOrderedAccordingToCreationTimePlusRequestedDelay(long create1, String delay1, long create2, String delay2, int expectedCompareResult) {
        ReflectionTestUtils.setField(Command.class, "clock", Clock.fixed(I0.plusSeconds(create1), ZoneOffset.UTC));
        var command1 = new ConcreteCommand(Duration.parse("PT" + delay1));
        ReflectionTestUtils.setField(Command.class, "clock", Clock.fixed(I0.plusSeconds(create2), ZoneOffset.UTC));
        var command2 = new ConcreteCommand(Duration.parse("PT" + delay2));

        var actualCompareResult = command1.compareTo(command2);

        assertEquals(expectedCompareResult, actualCompareResult);
    }

    static Stream<Arguments> mustBeOrderedAccordingToCreationTimePlusRequestedDelayArguments() {
        return Stream.of(
                Arguments.of(0L, "1s", 1L, "0s", 0),
                Arguments.of(0L, "0s", 0L, "5s", -1),
                Arguments.of(0L, "5s", 4L, "0s", 1)
        );
    }

    private static class ConcreteCommand extends Command {
        public ConcreteCommand(Duration delay) {
            super(delay);
        }

        @Override
        public void run() {
            // Do nothing!
        }
    }
}