package be.fror.projecteuler;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.junit.Assert.assertEquals;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertTrue;

import be.fror.projecteuler.ProblemRunner.Outcome;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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

    assertEquals(new Outcome.Solved("42", MILLISECONDS.toNanos(100)), result);
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

    assertEquals("42", ((Outcome.Solved) result).answer());
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
    assertEquals(new Outcome.Solved("null", MILLISECONDS.toNanos(400)),
        runner.run("Yours", FakeProblem.takingMillis(null, clock, 400)));
  }

  @Test
  public void reportsWhatTheSolutionThrows() {
    var failure = new IllegalStateException("broken solution");

    var outcome = runner.run("Yours", new ThrowingProblem(7, failure));

    assertEquals(new Outcome.Failed(failure), outcome);
    assertTrue(output(), output().startsWith(
        "\tYours:    failed: java.lang.IllegalStateException: broken solution"
            + " at be.fror.projecteuler.ProblemRunnerTest.reportsWhatTheSolutionThrows("));
  }

  @Test
  public void reportsErrorsAsFailuresToo() {
    var failure = new StackOverflowError();

    assertEquals(new Outcome.Failed(failure), runner.run("Yours", new ThrowingProblem(7, failure)));
  }

  private String output() {
    return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }
}
