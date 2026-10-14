package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Reciprocal cycles.
 *
 * <p>The recurring cycle of 1/d is as long as it takes the long-division remainders, starting from
 * 1, to come back to 1. That length is below d, so scanning d downwards lets the search stop as
 * soon as no smaller d can beat the best cycle found. Here that happens after about a dozen
 * candidates near the top, each costing one multiplication and remainder per cycle digit, with no
 * modular exponentiation and no prime sieve.
 */
public class Problem26 extends AbstractProblem {

  private static final int LIMIT = 1000;

  @Override
  public Object solve() {
    var bestD = 0;
    var bestLength = 0;
    for (var d = LIMIT - 1; d - 1 > bestLength; d--) {
      // Factors 2 and 5 only add a non-recurring prefix: such a d has the cycle of a smaller d.
      if (d % 2 == 0 || d % 5 == 0) {
        continue;
      }
      var length = cycleLength(d);
      if (length > bestLength) {
        bestLength = length;
        bestD = d;
      }
    }
    return bestD;
  }

  /** Cycle length of 1/d, for d coprime with 10. */
  private static int cycleLength(int d) {
    var length = 0;
    var remainder = 1;
    do {
      remainder = remainder * 10 % d;
      length++;
    } while (remainder != 1);
    return length;
  }
}
