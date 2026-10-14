package be.fror.projecteuler;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import be.fror.projecteuler.ProblemRunner.Outcome;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ProblemRunnerTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  private final FakeClock clock = new FakeClock();
  private final ProblemRunner runner =
      new ProblemRunner(executor, clock, new PrintStream(bytes, true, StandardCharsets.UTF_8));

  @AfterEach
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void fastSolutionIsWarmedUpThenMeasuredForOneSecond() {
    // Cold run, then ten 100 ms warm-up runs fill the first second and ten timed runs the next.
    var result = runner.run("Yours", FakeProblem.takingMillis(42, clock, 300, 100));

    assertThat(result).isEqualTo(new Outcome.Solved("42", MILLISECONDS.toNanos(100)));
    assertThat(output())
        .isEqualTo("\tYours:    42  median 100.0 ms  (min 100.0 ms, cold 300.0 ms, 10 runs)\n");
  }

  @Test
  public void reportsMedianAndMinimumOfTimedRuns() {
    // Cold 500, warm-up 400 + 400, then timed runs until the next one wouldn't fit in a second.
    runner.run("Yours", FakeProblem.takingMillis(42, clock, 500, 400, 400, 300, 100, 200, 600));

    assertThat(output())
        .isEqualTo("\tYours:    42  median 250.0 ms  (min 100.0 ms, cold 500.0 ms, 4 runs)\n");
  }

  @Test
  public void slowSolutionGetsOneTimedRunAndNoWarmUp() {
    runner.run("Yours", FakeProblem.takingMillis(42, clock, 2_000, 1_500));

    assertThat(output())
        .isEqualTo("\tYours:    42  median 1.500 s  (min 1.500 s, cold 2.000 s, 1 run)\n");
  }

  @Test
  public void fastSolutionIsTimedInBatches() {
    // 300 ns per run is too short to time alone: batches of 334 runs make 100.2 µs samples.
    runner.run("Yours", FakeProblem.takingNanos(42, clock, 300));

    assertThat(output())
        .isEqualTo("\tYours:    42  median 300 ns  (min 300 ns, cold 300 ns, 3333320 runs)\n");
  }

  @Test
  @Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  public void solutionTooFastToMeasureDoesNotBreakBatching() {
    // Like a solution the JIT reduces to a constant: only reading the clock takes time.
    var tickingClock = new FakeClock(MICROSECONDS.toNanos(1));
    var tickingRunner = new ProblemRunner(
        executor, tickingClock, new PrintStream(bytes, true, StandardCharsets.UTF_8));

    var result = tickingRunner.run("Yours", FakeProblem.takingNanos(42, tickingClock, 0));

    assertThat(((Outcome.Solved) result).answer()).isEqualTo("42");
  }

  @Test
  public void formatsDurationsWithFourSignificantDigits() {
    assertThat(ProblemRunner.format(0)).isEqualTo("< 1 ns");
    assertThat(ProblemRunner.format(999)).isEqualTo("999 ns");
    assertThat(ProblemRunner.format(1_000)).isEqualTo("1.000 µs");
    assertThat(ProblemRunner.format(256_789)).isEqualTo("256.8 µs");
    assertThat(ProblemRunner.format(8_061_000)).isEqualTo("8.061 ms");
    assertThat(ProblemRunner.format(2_000_000_000)).isEqualTo("2.000 s");
  }

  @Test
  public void returnsNullResultAsText() {
    assertThat(runner.run("Yours", FakeProblem.takingMillis(null, clock, 400)))
        .isEqualTo(new Outcome.Solved("null", MILLISECONDS.toNanos(400)));
  }

  @Test
  public void reportsWhatTheSolutionThrows() {
    var failure = new IllegalStateException("broken solution");

    var outcome = runner.run("Yours", new ThrowingProblem(7, failure));

    assertThat(outcome).isEqualTo(new Outcome.Failed(failure));
    assertThat(output()).startsWith(
        "\tYours:    failed: java.lang.IllegalStateException: broken solution"
            + " at be.fror.projecteuler.ProblemRunnerTest.reportsWhatTheSolutionThrows(");
  }

  @Test
  public void reportsErrorsAsFailuresToo() {
    var failure = new StackOverflowError();

    assertThat(runner.run("Yours", new ThrowingProblem(7, failure)))
        .isEqualTo(new Outcome.Failed(failure));
  }

  private String output() {
    return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }
}
