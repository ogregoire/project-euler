package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Even Fibonacci numbers.
 *
 * <p>Only every third Fibonacci number is even, and the even ones follow their own recurrence:
 * {@code E(k) = 4 E(k-1) + E(k-2)}, starting from 2 and 8. Walking that recurrence visits only the
 * terms that are summed, so there is no parity test and a third of the iterations.
 */
public class Problem2 extends AbstractProblem {

  private static final int LIMIT = 4_000_000;

  @Override
  public Object solve() {
    var sum = 0;
    var previous = 2;
    var current = 8;
    while (previous <= LIMIT) {
      sum += previous;
      var next = 4 * current + previous;
      previous = current;
      current = next;
    }
    return sum;
  }
}
