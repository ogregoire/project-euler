/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.math;

import java.math.BigInteger;
import java.util.stream.LongStream;

/**
 *
 * @author Olivier Grégoire
 */
public class Tools {

  private static final int[] POWERS_OF_10 = {
      1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000};

  private static final long[] LONG_POWERS_OF_10 =
      LongStream.iterate(1, p -> p * 10).limit(19).toArray();

  /**
   * Returns whether {@code n} reads the same both ways in base 10. Negative numbers never do.
   *
   * @param n any integer
   * @return {@code true} if {@code n} is a palindrome in base 10
   */
  public static boolean isPalindromic(int n) {
    if (n < 10) {
      return n >= 0;
    }
    // floor(log10(n)), from the bit length: 1233 / 4096 is close enough to log10(2) that the
    // estimate is either exact or one too high.
    var log10 = ((32 - Integer.numberOfLeadingZeros(n)) * 1233) >>> 12;
    if (n < POWERS_OF_10[log10]) {
      log10--;
    }
    // The first and last digits differ for about 90% of numbers: reject those with one division.
    if (n / POWERS_OF_10[log10] != n % 10) {
      return false;
    }
    // Reverse the lower half of the digits until it meets the upper half. The reversed half has
    // at most 5 digits, so it can't overflow. Dividing by the constant 10 compiles to a
    // multiplication.
    var reversed = 0;
    while (n > reversed) {
      reversed = reversed * 10 + n % 10;
      n /= 10;
    }
    // With an odd number of digits, the middle one ends up in reversed: drop it.
    return n == reversed || n == reversed / 10;
  }

  /**
   * Returns whether {@code n} reads the same both ways in base 10. Negative numbers never do.
   *
   * @param n any integer
   * @return {@code true} if {@code n} is a palindrome in base 10
   */
  public static boolean isPalindromic(long n) {
    if (n < 10) {
      return n >= 0;
    }
    // Same steps as for an int. The estimate stays exact or one too high up to 63 bits.
    var log10 = ((64 - Long.numberOfLeadingZeros(n)) * 1233) >>> 12;
    if (n < LONG_POWERS_OF_10[log10]) {
      log10--;
    }
    if (n / LONG_POWERS_OF_10[log10] != n % 10) {
      return false;
    }
    var reversed = 0L;
    while (n > reversed) {
      reversed = reversed * 10 + n % 10;
      n /= 10;
    }
    return n == reversed || n == reversed / 10;
  }

  /**
   * Returns whether the digits read the same both ways, for numbers too large for a {@code long}.
   *
   * @param digits the digits of a number, in either order
   * @return {@code true} if {@code digits} is a palindrome
   */
  public static boolean isPalindromic(int[] digits) {
    for (int lo = 0, hi = digits.length - 1; lo < hi; lo++, hi--) {
      if (digits[lo] != digits[hi]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns whether {@code n} reads the same both ways in base 2, without leading zeros. Negative
   * numbers never do.
   *
   * @param n any integer
   * @return {@code true} if {@code n} is a palindrome in base 2
   */
  public static boolean isBinaryPalindromic(int n) {
    // Reversing all 32 bits moves the leading zeros to the end: shifting them out leaves the
    // reversed binary digits. For 0, the shift by 32 is a shift by 0, which is still correct.
    return n >= 0 && Integer.reverse(n) >>> Integer.numberOfLeadingZeros(n) == n;
  }

  /**
   * Returns {@code n!}.
   *
   * @param n must be non-negative
   * @return the factorial of {@code n}
   */
  public static BigInteger factorial(int n) {
    BigInteger result = BigInteger.ONE;
    for (int i = 2; i <= n; i++) {
      result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
  }

}
