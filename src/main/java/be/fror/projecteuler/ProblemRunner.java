/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.LongSupplier;
import java.util.stream.LongStream;

/**
 * Solves problems and measures how long they take.
 *
 * <p>A single run mostly measures class loading and the interpreter. So after a first, "cold" run,
 * the problem is solved repeatedly: first untimed to let the JIT compile it, then timed.
 *
 * @author Olivier Grégoire
 */
public class ProblemRunner {

  private static final long WARM_UP_NANOS = SECONDS.toNanos(1);
  private static final long MEASUREMENT_NANOS = SECONDS.toNanos(1);
  private static final long MIN_SAMPLE_NANOS = MICROSECONDS.toNanos(100);

  private final ExecutorService executor;
  private final LongSupplier nanoClock;
  private final PrintStream output;

  /** Written with every answer, so that the JIT can't drop solutions whose answer is unused. */
  private volatile int sink;

  /**
   * Creates a runner.
   *
   * @param executor runs the solutions, so that a cold run taking too long can be abandoned.
   * @param nanoClock the clock used for timing, typically {@code System::nanoTime}.
   * @param output where results and timings are printed.
   */
  public ProblemRunner(ExecutorService executor, LongSupplier nanoClock, PrintStream output) {
    this.executor = executor;
    this.nanoClock = nanoClock;
    this.output = output;
  }

  /** What happened when running a solution. */
  public sealed interface Outcome {

    /**
     * The solution gave an answer.
     *
     * @param answer the answer as text, so that answers boxed in different types (Integer,
     *     Long...) can be compared.
     * @param medianNanos the median time of a warm run.
     */
    record Solved(String answer, long medianNanos) implements Outcome {}

    /** The cold run took more than one minute. */
    record TimedOut() implements Outcome {}

    /**
     * The solution threw.
     *
     * @param cause what the solution threw.
     */
    record Failed(Throwable cause) implements Outcome {}
  }

  /**
   * Solves the problem and prints its result and timings on one line, prefixed by the label.
   *
   * @param label who wrote the solution.
   * @param problem the problem to solve.
   * @return the outcome of the run.
   */
  public Outcome run(String label, Problem problem) {
    var prefix = String.format("\t%-9s", label + ":");
    var coldRun = executor.submit(() -> time(problem));
    try {
      var cold = coldRun.get(1, MINUTES);
      var measurement = executor.submit(() -> warmUpAndMeasure(problem, cold.nanos())).get();
      var result = String.valueOf(cold.result());
      var perRun = measurement.perRunNanos();
      var runs = measurement.runs();
      var median = median(perRun);
      output.printf("%s %s  median %s  (min %s, cold %s, %d run%s)%n",
          prefix, result, format(median), format(perRun[0]), format(cold.nanos()),
          runs, runs == 1 ? "" : "s");
      return new Outcome.Solved(result, median);
    } catch (TimeoutException e) {
      // Solutions don't check for interruption: this abandons the thread more than it stops it.
      coldRun.cancel(true);
      output.printf("%s failed to get a result under one minute.%n", prefix);
      return new Outcome.TimedOut();
    } catch (ExecutionException e) {
      // Any throwable, even an Error such as StackOverflowError, only means this solution failed.
      var cause = e.getCause();
      var where = cause.getStackTrace().length > 0 ? " at " + cause.getStackTrace()[0] : "";
      output.printf("%s failed: %s%s%n", prefix, cause, where);
      return new Outcome.Failed(cause);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while solving", e);
    }
  }

  /** Times at least one sample, more if they fit the budget. */
  private Measurement warmUpAndMeasure(Problem problem, long coldNanos) {
    // A new sample starts only if the previous one suggests it fits in the remaining budget,
    // so that slow solutions don't take several times longer to report.
    var lastNanos = coldNanos;
    var warmUpStart = nanoClock.getAsLong();
    while (nanoClock.getAsLong() - warmUpStart + lastNanos <= WARM_UP_NANOS) {
      lastNanos = time(problem, 1);
    }

    // The clock is too coarse for the fastest solutions (about 42 ns on Apple silicon), so each
    // sample times a batch of runs long enough to measure, and keeps the average run.
    var batch = (int) Math.ceilDiv(MIN_SAMPLE_NANOS, Math.max(lastNanos, 1));

    var perRunNanos = LongStream.builder();
    var samples = 0;
    var measurementStart = nanoClock.getAsLong();
    do {
      lastNanos = time(problem, batch);
      perRunNanos.add(lastNanos / batch);
      samples++;
    } while (nanoClock.getAsLong() - measurementStart + lastNanos <= MEASUREMENT_NANOS);
    return new Measurement(perRunNanos.build().sorted().toArray(), (long) samples * batch);
  }

  /** Returns how long it took to solve the problem {@code times} times in a row. */
  private long time(Problem problem, int times) {
    var answers = 0;
    var start = nanoClock.getAsLong();
    for (var i = 0; i < times; i++) {
      answers += Objects.hashCode(problem.solve());
    }
    var nanos = nanoClock.getAsLong() - start;
    sink = answers;
    return nanos;
  }

  private Run time(Problem problem) {
    var start = nanoClock.getAsLong();
    var result = problem.solve();
    return new Run(result, nanoClock.getAsLong() - start);
  }

  private record Run(Object result, long nanos) {}

  /** The sorted average run time of each sample, and how many runs all the samples made. */
  private record Measurement(long[] perRunNanos, long runs) {}

  private static long median(long[] sorted) {
    return (sorted[(sorted.length - 1) / 2] + sorted[sorted.length / 2]) / 2;
  }

  static String format(long nanos) {
    if (nanos == 0) {
      // Even a batch of runs was too fast to measure, typically because the JIT reduced the
      // solution to a constant.
      return "< 1 ns";
    }
    if (nanos < 1_000) {
      return nanos + " ns";
    }
    if (nanos < 1_000_000) {
      return String.format(Locale.ROOT, "%.4g µs", nanos / 1e3);
    }
    if (nanos < 1_000_000_000) {
      return String.format(Locale.ROOT, "%.4g ms", nanos / 1e6);
    }
    return String.format(Locale.ROOT, "%.4g s", nanos / 1e9);
  }
}
