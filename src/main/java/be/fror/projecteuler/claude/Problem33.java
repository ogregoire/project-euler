package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Digit cancelling fractions.
 *
 * <p>The only non-trivial pattern below 1 is {@code (10n + c) / (10c + d) = n / d}, with digits
 * {@code n < d}: cancelling the same place in both is trivial, and the mirrored placement only
 * gives these fractions inverted. Solving for the cancelled digit gives
 * {@code c = 9nd / (10n - d)}, so only digit pairs {@code (n, d)} are tried, in exact integer
 * arithmetic, instead of fractions.
 * Requiring {@code c <= 9} bounds {@code d} by {@code d (n + 1) <= 10n}, leaving 22 pairs.
 */
public class Problem33 extends AbstractProblem {

  @Override
  public Object solve() {
    var numerator = 1;
    var denominator = 1;
    for (var n = 1; n <= 9; n++) {
      for (var d = n + 1; d * (n + 1) <= 10 * n; d++) {
        if (9 * n * d % (10 * n - d) == 0) {
          numerator *= n;
          denominator *= d;
        }
      }
    }
    return denominator / gcd(numerator, denominator);
  }

  private static int gcd(int a, int b) {
    while (b != 0) {
      var remainder = a % b;
      a = b;
      b = remainder;
    }
    return a;
  }
}
