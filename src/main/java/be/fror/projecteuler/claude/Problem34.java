package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Digit factorials.
 *
 * <p>The sum of the factorials of the digits does not depend on their order, so instead of
 * testing every number up to the bound (millions of them), this enumerates multisets of digits
 * (about twenty thousand) and checks whether the factorial sum of each one is written with exactly
 * those digits.
 *
 * <p>Digits are chosen in non-increasing order, so the largest sum still reachable is known at each
 * step; branches that cannot reach the required number of digits are cut early.
 */
public class Problem34 extends AbstractProblem {

  private static final int[] FACTORIALS = new int[10];

  static {
    FACTORIALS[0] = 1;
    for (var d = 1; d < FACTORIALS.length; d++) {
      FACTORIALS[d] = FACTORIALS[d - 1] * d;
    }
  }

  @Override
  public Object solve() {
    var total = 0;
    var low = 10;
    // One-digit numbers are excluded: "1! = 1" and "2! = 2" are not sums.
    // A number with d digits is at least 10^(d - 1) but its sum is at most d·9!, which bounds d.
    for (var digits = 2; low <= digits * FACTORIALS[9]; digits++, low *= 10) {
      total += search(digits, 9, 0, 0L, low, low * 10);
    }
    return total;
  }

  /**
   * Returns the sum of the numbers in [low, high) equal to the factorial sum of their digits, among
   * those made of the digits already chosen (summing to {@code sum}, counted in
   * {@code signature}) plus {@code remaining} more digits, none larger than {@code maxDigit}.
   */
  private static int search(
      int remaining, int maxDigit, int sum, long signature, int low, int high) {
    if (remaining == 0) {
      return sum >= low && sum < high && signatureOf(sum) == signature ? sum : 0;
    }
    if (sum + remaining * FACTORIALS[maxDigit] < low || sum + remaining >= high) {
      return 0;
    }
    var total = 0;
    for (var d = maxDigit; d >= 0; d--) {
      total += search(
          remaining - 1, d, sum + FACTORIALS[d], signature + digitSignature(d), low, high);
    }
    return total;
  }

  /** Counts each digit in its own 4-bit field: two numbers match iff they use the same digits. */
  private static long signatureOf(int n) {
    var signature = 0L;
    for (; n > 0; n /= 10) {
      signature += digitSignature(n % 10);
    }
    return signature;
  }

  private static long digitSignature(int digit) {
    return 1L << (4 * digit);
  }
}
