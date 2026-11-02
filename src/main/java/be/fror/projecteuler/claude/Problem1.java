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

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int limit = 1000;

  @Override
  public Object solve() {
    var limit = this.limit;
    return sumOfMultiples(3, limit) + sumOfMultiples(5, limit) - sumOfMultiples(15, limit);
  }

  /** Sum of the positive multiples of {@code n} strictly below {@code limit}. */
  private static int sumOfMultiples(int n, int limit) {
    var count = (limit - 1) / n;
    return n * count * (count + 1) / 2;
  }
}
