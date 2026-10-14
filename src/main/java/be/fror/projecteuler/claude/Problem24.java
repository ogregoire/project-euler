package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Lexicographic permutations.
 *
 * <p>Writes the index (one million minus one, zero-based) in the factorial number system: each
 * factorial digit says which of the remaining digits comes next. The remaining digits are kept in
 * order as 4-bit nibbles of a single {@code long}, so taking one out is a couple of shifts and
 * masks instead of shuffling an array.
 *
 * <p>The answer is returned as a number: it cannot start with 0, because the index is at least
 * {@code 9!}, so its first factorial digit is not zero.
 */
public class Problem24 extends AbstractProblem {

  private static final int DIGITS = 10;
  private static final int POSITION = 1_000_000;

  @Override
  public Object solve() {
    var factorial = 1;
    for (var i = 2; i < DIGITS; i++) {
      factorial *= i;
    }
    var remaining = 0x9876543210L;
    var index = POSITION - 1;
    var result = 0L;
    for (var left = DIGITS - 1; left >= 0; left--) {
      var shift = 4 * (index / factorial);
      index %= factorial;
      result = result * 10 + ((remaining >>> shift) & 0xF);
      var lowMask = (1L << shift) - 1;
      remaining = (remaining & lowMask) | ((remaining >>> (shift + 4)) << shift);
      if (left > 0) {
        factorial /= left;
      }
    }
    return result;
  }
}
