package be.fror.projecteuler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ApplicationTest {

  private static final String TIMINGS = "  median 400.0 ms  (min 400.0 ms, cold 400.0 ms, 2 runs)";

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errorBytes = new ByteArrayOutputStream();
  private final FakeClock clock = new FakeClock();

  @AfterEach
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void asksWhichProblemsWithoutArguments() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of());

    var exitCode = start(factory, "7");

    assertThat(exitCode).isEqualTo(0);
    assertThat(output()).isEqualTo(
        "Enter problems (e.g. 23, 1-10 or all): \n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\n");
  }

  @Test
  public void runsBothSolutions() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of(problem(7, 42L)));

    var exitCode = start(factory, "", "7");

    assertThat(exitCode).isEqualTo(0);
    assertThat(output()).isEqualTo(
        "\n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\tClaude's: 42" + TIMINGS + "\n"
            + "\n");
  }

  @Test
  public void reportsDifferentResults() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of(problem(7, 43)));

    var exitCode = start(factory, "", "7");

    assertThat(exitCode).isEqualTo(Application.EXIT_FAILURE);
    assertThat(output()).isEqualTo(
        "\n"
            + "Running problem 7...\n"
            + "\tYours:    42" + TIMINGS + "\n"
            + "\tClaude's: 43" + TIMINGS + "\n"
            + "\tResults differ!\n"
            + "\n");
  }

  @Test
  public void printsNullResult() {
    var factory = new ProblemFactory(List.of(problem(7, null)), List.of(problem(7, null)));

    start(factory, "", "7");

    assertThat(output()).isEqualTo(
        "\n"
            + "Running problem 7...\n"
            + "\tYours:    null" + TIMINGS + "\n"
            + "\tClaude's: null" + TIMINGS + "\n"
            + "\n");
  }

  @Test
  public void runsSelectionThenPrintsSummary() {
    var claudeProblem = FakeProblem.takingMillis(42, clock, 100).withId(2);
    var factory = new ProblemFactory(
        List.of(problem(1, 41), problem(2, 42), problem(3, 43)), List.of(claudeProblem));

    var exitCode = start(factory, "", "1-2");

    assertThat(exitCode).isEqualTo(0);
    assertThat(output()).isEqualTo(
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
            + "      2    400.0 ms    100.0 ms  4.0× faster\n");
  }

  @Test
  public void keepsGoingAfterFailure() {
    var factory = new ProblemFactory(
        List.of(new ThrowingProblem(1, new IllegalStateException("broken")), problem(2, 42)),
        List.of());

    var exitCode = start(factory, "", "all");

    assertThat(exitCode).isEqualTo(Application.EXIT_FAILURE);
    var output = output();
    assertThat(output)
        .contains("\tYours:    failed: java.lang.IllegalStateException: broken")
        .contains("\tYours:    42" + TIMINGS + "\n")
        .endsWith("Problem       Yours    Claude's\n"
            + "      1      failed\n"
            + "      2    400.0 ms\n");
  }

  @Test
  public void rejectsInvalidSelection() {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of());

    var exitCode = start(factory, "", "8");

    assertThat(exitCode).isEqualTo(Application.EXIT_USAGE);
    assertThat(output()).isEmpty();
    assertThat(errors()).isEqualTo("Problem 8 not found.\n" + ProblemSelection.USAGE + "\n");
  }

  @ParameterizedTest
  @ValueSource(strings = {"-h", "--help"})
  public void printsRequestedUsage(String option) {
    var factory = new ProblemFactory(List.of(problem(7, 42)), List.of());

    var exitCode = start(factory, "", option);

    assertThat(exitCode).isEqualTo(0);
    assertThat(output()).isEqualTo(ProblemSelection.USAGE + "\n");
    assertThat(errors()).isEmpty();
  }

  /** Every run takes 400 ms: two fit in the one-second warm-up, two in the measurement. */
  private Problem problem(int id, Object answer) {
    return FakeProblem.takingMillis(answer, clock, 400).withId(id);
  }

  private int start(ProblemFactory problemFactory, String input, String... args) {
    var out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
    var runner = new ProblemRunner(executor, clock, out);
    var err = new PrintStream(errorBytes, true, StandardCharsets.UTF_8);
    return new Application(out, err, new Scanner(input), problemFactory, runner).start(args);
  }

  private String output() {
    return text(bytes);
  }

  private String errors() {
    return text(errorBytes);
  }

  private static String text(ByteArrayOutputStream bytes) {
    return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }
}
