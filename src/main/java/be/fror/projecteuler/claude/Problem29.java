package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Distinct powers.
 *
 * <p>Counts the distinct terms instead of generating them. Write each base as {@code a = r^k}
 * where {@code r} is not itself a perfect power; then {@code a^b = r^(kb)}, and two terms can only
 * be equal when they share the same root {@code r}. So the terms of root {@code r} are as many as
 * the distinct exponents {@code kb}, for {@code 1 <= k <= K} and {@code 2 <= b <= 100}, where
 * {@code r^K} is the largest power of {@code r} within the limit.
 *
 * <p>That count only depends on {@code K}, which is at most 6, so it is computed once per {@code
 * K} on a small array of exponents, and then each root just adds the count for its own {@code K}.
 */
public class Problem29 extends AbstractProblem {

  private static final int LIMIT = 100;

  @Override
  public Object solve() {
    var maxK = 31 - Integer.numberOfLeadingZeros(LIMIT);

    // distinctExponents[k]: how many distinct values k'b takes, for k' <= k and 2 <= b <= LIMIT.
    var distinctExponents = new int[maxK + 1];
    var seen = new boolean[maxK * LIMIT + 1];
    for (var k = 1; k <= maxK; k++) {
      var count = distinctExponents[k - 1];
      for (var b = 2; b <= LIMIT; b++) {
        if (!seen[k * b]) {
          seen[k * b] = true;
          count++;
        }
      }
      distinctExponents[k] = count;
    }

    var isPerfectPower = new boolean[LIMIT + 1];
    var total = 0;
    for (var root = 2; root <= LIMIT; root++) {
      if (isPerfectPower[root]) {
        continue;
      }
      var k = 1;
      for (var power = root * root; power <= LIMIT; power *= root) {
        isPerfectPower[power] = true;
        k++;
      }
      total += distinctExponents[k];
    }
    return total;
  }
}
