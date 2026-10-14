package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Permuted multiples.
 *
 * <p>Two arrangements of the same digits have the same digit sum, hence the same value mod 9. So
 * {@code x ≡ 2x (mod 9)}, which forces {@code x} to be a multiple of 9: only one candidate in
 * nine needs to be checked. Also, {@code 6x} must have as many digits as {@code x}, so with
 * {@code d} digits, {@code x} cannot exceed {@code 10^d / 6}.
 */
public class Problem52 extends AbstractProblem {

  private static final int LARGEST_MULTIPLIER = 6;

  @Override
  public Object solve() {
    for (var start = 1; ; start *= 10) {
      var end = start * 10 / LARGEST_MULTIPLIER;
      var firstMultipleOf9 = (start + 8) / 9 * 9;
      for (var x = firstMultipleOf9; x <= end; x += 9) {
        if (hasPermutedMultiples(x)) {
          return x;
        }
      }
    }
  }

  private static boolean hasPermutedMultiples(int x) {
    var signature = signatureOf(x);
    for (var multiplier = 2; multiplier <= LARGEST_MULTIPLIER; multiplier++) {
      if (signatureOf(x * multiplier) != signature) {
        return false;
      }
    }
    return true;
  }

  /** Counts each digit in its own 4-bit field: two numbers match iff they use the same digits. */
  private static long signatureOf(int n) {
    var signature = 0L;
    for (; n > 0; n /= 10) {
      signature += 1L << (4 * (n % 10));
    }
    return signature;
  }
}
