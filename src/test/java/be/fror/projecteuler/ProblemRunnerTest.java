package be.fror.projecteuler;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Test;

public class ProblemRunnerTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  private final FakeClock clock = new FakeClock();
  private final ProblemRunner runner =
      new ProblemRunner(executor, clock, new PrintStream(bytes, true, StandardCharsets.UTF_8));

  @After
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void fastSolutionIsWarmedUpThenMeasuredForOneSecond() {
    // Cold run, then ten 100 ms warm-up runs fill the first second and ten timed runs the next.
    var result = runner.run("Yours", FakeProblem.takingMillis(42, clock, 300, 100));

    assertEquals(Optional.of("42"), result);
    assertEquals(
        "\tYours:    42  median 100.0 ms  (min 100.0 ms, cold 300.0 ms, 10 runs)\n", output());
  }

  @Test
  public void reportsMedianAndMinimumOfTimedRuns() {
    // Cold 500, warm-up 400 + 400, then timed runs until the next one wouldn't fit in a second.
    runner.run("Yours", FakeProblem.takingMillis(42, clock, 500, 400, 400, 300, 100, 200, 600));

    assertEquals(
        "\tYours:    42  median 250.0 ms  (min 100.0 ms, cold 500.0 ms, 4 runs)\n", output());
  }

  @Test
  public void slowSolutionGetsOneTimedRunAndNoWarmUp() {
    runner.run("Yours", FakeProblem.takingMillis(42, clock, 2_000, 1_500));

    assertEquals(
        "\tYours:    42  median 1.500 s  (min 1.500 s, cold 2.000 s, 1 run)\n", output());
  }

  @Test
  public void fastSolutionIsTimedInBatches() {
    // 300 ns per run is too short to time alone: batches of 334 runs make 100.2 µs samples.
    runner.run("Yours", FakeProblem.takingNanos(42, clock, 300));

    assertEquals(
        "\tYours:    42  median 300 ns  (min 300 ns, cold 300 ns, 3333320 runs)\n", output());
  }

  @Test(timeout = 10_000)
  public void solutionTooFastToMeasureDoesNotBreakBatching() {
    // Like a solution the JIT reduces to a constant: only reading the clock takes time.
    var tickingClock = new FakeClock(MICROSECONDS.toNanos(1));
    var tickingRunner = new ProblemRunner(
        executor, tickingClock, new PrintStream(bytes, true, StandardCharsets.UTF_8));

    var result = tickingRunner.run("Yours", FakeProblem.takingNanos(42, tickingClock, 0));

    assertEquals(Optional.of("42"), result);
  }

  @Test
  public void formatsDurationsWithFourSignificantDigits() {
    assertEquals("< 1 ns", ProblemRunner.format(0));
    assertEquals("999 ns", ProblemRunner.format(999));
    assertEquals("1.000 µs", ProblemRunner.format(1_000));
    assertEquals("256.8 µs", ProblemRunner.format(256_789));
    assertEquals("8.061 ms", ProblemRunner.format(8_061_000));
    assertEquals("2.000 s", ProblemRunner.format(2_000_000_000));
  }

  @Test
  public void returnsNullResultAsText() {
    assertEquals(Optional.of("null"), runner.run("Yours", FakeProblem.takingMillis(null, clock, 400)));
  }

  @Test
  public void rethrowsWhatTheSolutionThrows() {
    var failure = new IllegalStateException("broken solution");

    var thrown = assertThrows(IllegalStateException.class,
        () -> runner.run("Yours", new ThrowingProblem(failure)));

    assertSame(failure, thrown);
  }

  private String output() {
    return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }

  private static final class ThrowingProblem implements Problem {

    private final RuntimeException failure;

    ThrowingProblem(RuntimeException failure) {
      this.failure = failure;
    }

    @Override
    public int getId() {
      return 7;
    }

    @Override
    public Object solve() {
      throw failure;
    }

    @Override
    public Object call() {
      return solve();
    }
  }
}
