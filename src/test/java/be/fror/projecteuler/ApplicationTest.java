package be.fror.projecteuler;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Test;

public class ApplicationTest {

  private static final String TIMINGS = "  median 400.0 ms  (min 400.0 ms, cold 400.0 ms, 2 runs)";

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  private final FakeClock clock = new FakeClock();

  @After
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void runsOnlyOriginalSolutionWhenClaudeHasNone() {
    run(new FakeProblemFactory(problem(42), null));

    assertEquals(
        "Enter problem number: \n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\n",
        output());
  }

  @Test
  public void runsBothSolutions() {
    run(new FakeProblemFactory(problem(42), problem(42L)));

    assertEquals(
        "Enter problem number: \n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\tClaude's: 42" + TIMINGS + "\n"
            + "\n",
        output());
  }

  @Test
  public void reportsDifferentResults() {
    run(new FakeProblemFactory(problem(42), problem(43)));

    assertEquals(
        "Enter problem number: \n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\tClaude's: 43" + TIMINGS + "\n"
            + "\tResults differ!\n"
            + "\n",
        output());
  }

  @Test
  public void printsNullResult() {
    run(new FakeProblemFactory(problem(null), problem(null)));

    assertEquals(
        "Enter problem number: \n"
            + "Running problem 7...\n"
            + "\tYours:    null" + TIMINGS + "\n"
            + "\tClaude's: null" + TIMINGS + "\n"
            + "\n",
        output());
  }

  /** Every run takes 400 ms: two fit in the one-second warm-up, two in the measurement. */
  private Problem problem(Object answer) {
    return FakeProblem.takingMillis(answer, clock, 400);
  }

  private void run(ProblemFactory problemFactory) {
    var out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
    var runner = new ProblemRunner(executor, clock, out);
    new Application(out, new Scanner("7"), problemFactory, runner).start();
  }

  private String output() {
    return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }

  private static final class FakeProblemFactory extends ProblemFactory {

    private final Problem problem;
    private final Problem claudeProblem;

    FakeProblemFactory(Problem problem, Problem claudeProblem) {
      this.problem = problem;
      this.claudeProblem = claudeProblem;
    }

    @Override
    public Problem getProblem(int id) {
      return problem;
    }

    @Override
    public Optional<Problem> getClaudeProblem(int id) {
      return Optional.ofNullable(claudeProblem);
    }
  }
}
