package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Factorial digit sum.
 *
 * <p>The factorial is built directly in base 10<sup>14</sup>, so each limb already holds 14
 * decimal digits: summing digits needs no base conversion, unlike {@code BigInteger.toString()}.
 * Factors are multiplied in two at a time, halving the passes over the limbs: a limb times two
 * factors up to 100 (less than 10<sup>4</sup>), plus carry, still fits in a {@code long}.
 */
public class Problem20 extends AbstractProblem {

  private static final int N = 100;

  private static final long BASE = 100_000_000_000_000L;

  @Override
  public Object solve() {
    // N! < N^N = 10^(2N) for N = 100, so it has at most 2N digits.
    var limbs = new long[2 * N / 14 + 1];
    limbs[0] = 1;
    var size = 1;
    for (var factor = 2; factor <= N; factor += 2) {
      var multiplier = factor < N ? factor * (factor + 1) : factor;
      var carry = 0L;
      for (var i = 0; i < size; i++) {
        var value = limbs[i] * multiplier + carry;
        limbs[i] = value % BASE;
        carry = value / BASE;
      }
      if (carry != 0) {
        limbs[size++] = carry;
      }
    }

    var sum = 0;
    for (var i = 0; i < size; i++) {
      for (var limb = limbs[i]; limb != 0; limb /= 10) {
        sum += (int) (limb % 10);
      }
    }
    return sum;
  }
}
