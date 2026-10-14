/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.PrintStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Olivier Grégoire
 */
public class ProblemRunner {

  private final TimeLimiter timeLimiter;
  private final Provider<Stopwatch> stopwatchProvider;
  private final PrintStream output;

  @Inject
  public ProblemRunner(TimeLimiter timeLimiter, Provider<Stopwatch> stopwatchProvider,
      PrintStream output) {
    this.timeLimiter = timeLimiter;
    this.stopwatchProvider = stopwatchProvider;
    this.output = output;
  }

  /**
   * Solves the problem and prints its result and solving time on one line, prefixed by the label.
   *
   * @param label who wrote the solution.
   * @param problem the problem to solve.
   * @return the result, or an empty optional if the problem took more than one minute.
   */
  public Optional<Object> run(String label, Problem problem) {
    Stopwatch stopwatch = stopwatchProvider.get();

    Problem timeLimitedProblem = timeLimiter.newProxy(problem, Problem.class, 1, TimeUnit.MINUTES);
    try {
      Object result;
      stopwatch.start();
      result = timeLimitedProblem.solve();
      stopwatch.stop();

      output.printf("\t%-9s %s in %s%n", label + ":", result, stopwatch);
      return Optional.of(result);
    } catch (UncheckedTimeoutException e) {
      output.printf("\t%-9s failed to get a result under one minute.%n", label + ":");
      return Optional.empty();
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
}
