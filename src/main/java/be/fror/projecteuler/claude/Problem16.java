package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Power digit sum.
 *
 * <p>Computes 2^1000 directly in decimal, as limbs of 9 digits, so no binary-to-decimal
 * conversion is ever needed. Multiplying by 2^30 at a time instead of doubling divides the work by
 * 30, and a limb times 2^30 plus a carry still fits in a {@code long}.
 */
public class Problem16 extends AbstractProblem {

  private static final int EXPONENT = 1000;

  private static final int BITS_PER_STEP = 30;
  private static final long BASE = 1_000_000_000L;

  @Override
  public Object solve() {
    var limbs = new long[(int) (EXPONENT * Math.log10(2)) / 9 + 1];
    limbs[0] = 1;
    var used = 1;
    for (var remaining = EXPONENT; remaining > 0; remaining -= BITS_PER_STEP) {
      var factor = 1L << Math.min(remaining, BITS_PER_STEP);
      var carry = 0L;
      for (var i = 0; i < used; i++) {
        var product = limbs[i] * factor + carry;
        limbs[i] = product % BASE;
        carry = product / BASE;
      }
      if (carry != 0) {
        limbs[used++] = carry;
      }
    }

    var sum = 0;
    for (var i = 0; i < used; i++) {
      for (var limb = limbs[i]; limb != 0; limb /= 10) {
        sum += (int) (limb % 10);
      }
    }
    return sum;
  }
}
