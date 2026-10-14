package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Largest palindrome product.
 *
 * <p>Instead of trying products of two 3-digit numbers, this walks the 6-digit palindromes from
 * the largest down and stops at the first one with a suitable factorization, so only a few
 * palindromes near the answer are ever examined.
 *
 * <p>Checking a palindrome is cheap because every palindrome {@code abccba} equals {@code
 * 11 * (9091a + 910b + 100c)}: since 11 is prime, one of the two factors is a multiple of 11, so
 * only the handful of 3-digit multiples of 11 that keep the cofactor below 1000 need testing.
 */
public class Problem4 extends AbstractProblem {

  private static final int MIN_FACTOR = 100;
  private static final int MAX_FACTOR = 999;

  /** The largest multiple of 11 that is a 3-digit number. */
  private static final int MAX_MULTIPLE_OF_11 = MAX_FACTOR - MAX_FACTOR % 11;

  @Override
  public Object solve() {
    for (var a = 9; a >= 1; a--) {
      for (var b = 9; b >= 0; b--) {
        for (var c = 9; c >= 0; c--) {
          var palindrome = 100001 * a + 10010 * b + 1100 * c;
          if (isProductOfTwo3DigitNumbers(palindrome)) {
            return palindrome;
          }
        }
      }
    }
    throw new IllegalStateException("No 6-digit palindrome is a product of two 3-digit numbers");
  }

  private static boolean isProductOfTwo3DigitNumbers(int palindrome) {
    // Going down, the cofactor grows: once it exceeds 3 digits, smaller factors can't work either.
    for (var factor = MAX_MULTIPLE_OF_11; factor >= MIN_FACTOR; factor -= 11) {
      var cofactor = palindrome / factor;
      if (cofactor > MAX_FACTOR) {
        return false;
      }
      if (cofactor * factor == palindrome && cofactor >= MIN_FACTOR) {
        return true;
      }
    }
    return false;
  }
}
