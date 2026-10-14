package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Multiples of 3 or 5.
 *
 * <p>Inclusion-exclusion over arithmetic series: the multiples of 3 plus the multiples of 5, minus
 * the multiples of 15 that were counted twice. Each series has a closed-form sum, so the answer
 * takes a handful of operations instead of a loop over every number below the limit.
 */
public class Problem1 extends AbstractProblem {

  private static final int LIMIT = 1000;

  @Override
  public Object solve() {
    return sumOfMultiples(3) + sumOfMultiples(5) - sumOfMultiples(15);
  }

  /** Sum of the positive multiples of {@code n} strictly below {@link #LIMIT}. */
  private static int sumOfMultiples(int n) {
    var count = (LIMIT - 1) / n;
    return n * count * (count + 1) / 2;
  }
}
