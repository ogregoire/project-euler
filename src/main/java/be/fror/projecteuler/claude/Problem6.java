package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Sum square difference.
 *
 * <p>Both sums have closed forms, so the answer takes a handful of arithmetic operations instead
 * of a loop: {@code 1 + ... + n = n(n + 1) / 2} and {@code 1² + ... + n² = n(n + 1)(2n + 1) / 6}.
 */
public class Problem6 extends AbstractProblem {

  private static final int LIMIT = 100;

  @Override
  public Object solve() {
    var sum = LIMIT * (LIMIT + 1) / 2;
    var sumOfSquares = LIMIT * (LIMIT + 1) * (2 * LIMIT + 1) / 6;
    return sum * sum - sumOfSquares;
  }
}
