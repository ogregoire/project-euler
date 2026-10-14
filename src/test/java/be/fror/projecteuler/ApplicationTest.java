package be.fror.projecteuler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
  public void asksWhichProblemsWithoutArguments() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of());

    var exitCode = start(factory, "7");

    assertEquals(0, exitCode);
    assertEquals(
        "Enter problems (e.g. 23, 1-10 or all): \n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\n",
        output());
  }

  @Test
  public void runsBothSolutions() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of(problem(7, 42L)));

    var exitCode = start(factory, "", "7");

    assertEquals(0, exitCode);
    assertEquals(
        "\n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\tClaude's: 42" + TIMINGS + "\n"
            + "\n",
        output());
  }

  @Test
  public void reportsDifferentResults() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of(problem(7, 43)));

    var exitCode = start(factory, "", "7");

    assertEquals(Application.EXIT_FAILURE, exitCode);
    assertEquals(
        "\n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\tClaude's: 43" + TIMINGS + "\n"
            + "\tResults differ!\n"
            + "\n",
        output());
  }

  @Test
  public void printsNullResult() {
    var factory = new ProblemFactory(List.of(problem(7, null)), List.of(problem(7, null)));

    start(factory, "", "7");

    assertEquals(
        "\n"
            + "Running problem 7...\n"
            + "\tYours:    null" + TIMINGS + "\n"
            + "\tClaude's: null" + TIMINGS + "\n"
            + "\n",
        output());
  }

  @Test
  public void runsSelectionThenPrintsSummary() {
    var claudeProblem = FakeProblem.takingMillis(42, clock, 100).withId(2);
    var factory = new ProblemFactory(
        List.of(problem(1, 41), problem(2, 42), problem(3, 43)), List.of(claudeProblem));

    var exitCode = start(factory, "", "1-2");

    assertEquals(0, exitCode);
    assertEquals(
        "\n"
            + "Running problem 1...\n"
            + "\tYours:    41" + TIMINGS + "\n"
            + "\n"
            + "Running problem 2...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\tClaude's: 42  median 100.0 ms  (min 100.0 ms, cold 100.0 ms, 10 runs)\n"
            + "\n"
            + "Problem       Yours    Claude's\n"
            + "      1    400.0 ms\n"
            + "      2    400.0 ms    100.0 ms  4.0× faster\n",
        output());
  }

  @Test
  public void keepsGoingAfterFailure() {
    var factory = new ProblemFactory(
        List.of(new ThrowingProblem(1, new IllegalStateException("broken")), problem(2, 42)),
        List.of());

    var exitCode = start(factory, "", "all");

    assertEquals(Application.EXIT_FAILURE, exitCode);
    var output = output();
    assertTrue(output, output.contains("\tYours:    failed: java.lang.IllegalStateException: broken"));
    assertTrue(output, output.contains("\tYours:    42" + TIMINGS + "\n"));
    assertTrue(output, output.endsWith(
        "Problem       Yours    Claude's\n"
            + "      1      failed\n"
            + "      2    400.0 ms\n"));
  }

  @Test
  public void rejectsInvalidSelection() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of());

    var exitCode = start(factory, "", "8");

    assertEquals(Application.EXIT_USAGE, exitCode);
    assertEquals("Problem 8 not found.\n" + ProblemSelection.USAGE + "\n", output());
  }

  /** Every run takes 400 ms: two fit in the one-second warm-up, two in the measurement. */
  private Problem problem(int id, Object answer) {
    return FakeProblem.takingMillis(answer, clock, 400).withId(id);
  }

  private int start(ProblemFactory problemFactory, String input, String... args) {
    var out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
    var runner = new ProblemRunner(executor, clock, out);
    return new Application(out, new Scanner(input), problemFactory, runner).start(args);
  }

  private String output() {
    return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }
}
