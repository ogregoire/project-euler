package be.fror.projecteuler;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.util.concurrent.SimpleTimeLimiter;

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

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

  @After
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void runsOnlyOriginalSolutionWhenClaudeHasNone() {
    run(new FakeProblemFactory(new FakeProblem(7, 42), null));

    assertEquals(
        "Enter problem number: \n"
            + "Running problem 7...\n"
            + "\tYours:    42 in 1.000 ms\n"
            + "\n",
        output());
  }

  @Test
  public void runsBothSolutions() {
    run(new FakeProblemFactory(new FakeProblem(7, 42), new FakeProblem(7, 42L)));

    assertEquals(
        "Enter problem number: \n"
            + "Running problem 7...\n"
            + "\tYours:    42 in 1.000 ms\n"
            + "\tClaude's: 42 in 1.000 ms\n"
            + "\n",
        output());
  }

  @Test
  public void reportsDifferentResults() {
    run(new FakeProblemFactory(new FakeProblem(7, 42), new FakeProblem(7, 43)));

    assertEquals(
        "Enter problem number: \n"
            + "Running problem 7...\n"
            + "\tYours:    42 in 1.000 ms\n"
            + "\tClaude's: 43 in 1.000 ms\n"
            + "\tResults differ!\n"
            + "\n",
        output());
  }

  private void run(ProblemFactory problemFactory) {
    var out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
    var runner = new ProblemRunner(
        SimpleTimeLimiter.create(executor), () -> Stopwatch.createUnstarted(new OneMillisecondTicker()), out);
    new Application(out, new Scanner("7"), problemFactory, runner).start();
  }

  private String output() {
    return bytes.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }

  /** Each read advances by one millisecond, so a start/stop pair always measures exactly 1 ms. */
  private static final class OneMillisecondTicker extends Ticker {

    private long nanos;

    @Override
    public long read() {
      nanos += MILLISECONDS.toNanos(1);
      return nanos;
    }
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

  private record FakeProblem(int id, Object answer) implements Problem {

    @Override
    public int getId() {
      return id;
    }

    @Override
    public Object solve() {
      return answer;
    }

    @Override
    public Object call() {
      return answer;
    }
  }
}
