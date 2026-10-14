package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Sub-string divisibility.
 *
 * <p>Builds the numbers from the right: d8d9d10 is a multiple of 17 with distinct digits, then
 * each digit prepended must make the new leading three-digit group divisible by the next prime
 * (13, 11, 7, 5, 3, 2). The divisibility constraints prune almost every branch early, so only a
 * tiny fraction of the 3.6 million permutations is ever visited. The used digits are kept in a bit
 * mask, so nothing is allocated.
 */
public class Problem43 extends AbstractProblem {

  /** The divisors of d7d8d9 down to d2d3d4, in the order the digits are prepended. */
  private static final int[] DIVISORS = {13, 11, 7, 5, 3, 2};

  private static final int ALL_DIGITS = (1 << 10) - 1;

  @Override
  public Object solve() {
    var sum = 0L;
    for (var multiple = 0; multiple < 1000; multiple += 17) {
      var d8 = multiple / 100;
      var d9 = multiple / 10 % 10;
      var d10 = multiple % 10;
      var used = 1 << d8 | 1 << d9 | 1 << d10;
      if (Integer.bitCount(used) == 3) {
        sum += sumOfPrefixes(0, multiple / 10, multiple, 1000, used);
      }
    }
    return sum;
  }

  /**
   * Sums the pandigital numbers ending with {@code suffix} that satisfy all the constraints.
   *
   * @param depth how many divisors of {@link #DIVISORS} are already satisfied.
   * @param leadingPair the first two digits of the suffix, as a number.
   * @param suffix the digits placed so far, as a number.
   * @param scale the power of ten for the next digit to prepend.
   * @param used a bit mask of the digits in the suffix.
   */
  private static long sumOfPrefixes(int depth, int leadingPair, long suffix, long scale, int used) {
    if (depth == DIVISORS.length) {
      // Only one digit is left: d1.
      var d1 = Integer.numberOfTrailingZeros(~used & ALL_DIGITS);
      return d1 * scale + suffix;
    }
    var sum = 0L;
    for (var digit = 0; digit < 10; digit++) {
      if ((used & 1 << digit) == 0 && (digit * 100 + leadingPair) % DIVISORS[depth] == 0) {
        sum += sumOfPrefixes(depth + 1, digit * 10 + leadingPair / 10, digit * scale + suffix,
            scale * 10, used | 1 << digit);
      }
    }
    return sum;
  }
}
