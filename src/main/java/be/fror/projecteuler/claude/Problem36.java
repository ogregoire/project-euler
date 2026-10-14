package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Double-base palindromes.
 *
 * <p>Instead of testing every number below one million, this builds only the decimal palindromes
 * (fewer than 2000) digit by digit, adding each digit's place weight so no division is needed. A
 * binary palindrome cannot end with a 0 bit, so it is odd, and since a decimal palindrome starts
 * with its last digit, only odd leading digits are tried, halving the candidates.
 *
 * <p>The binary check is branch-free: reversing all 32 bits and shifting out the leading zeros
 * gives back the number itself exactly when it is a binary palindrome.
 */
public class Problem36 extends AbstractProblem {

  /** Numbers below one million have at most six decimal digits. */
  private static final int MAX_DIGITS = 6;

  @Override
  public Object solve() {
    var sum = 0L;
    for (var length = 1; length <= MAX_DIGITS; length++) {
      sum += sumDoublePalindromes(placeWeights(length), 0, 0);
    }
    return sum;
  }

  /**
   * Weight of each independent digit of a palindrome of the given length, outermost first: a
   * digit appears at both mirrored places, except the middle digit of an odd length.
   */
  private static int[] placeWeights(int length) {
    var weights = new int[(length + 1) / 2];
    var low = 1;
    var high = 1;
    for (var i = 1; i < length; i++) {
      high *= 10;
    }
    for (var i = 0; i < weights.length; i++, low *= 10, high /= 10) {
      weights[i] = low == high ? low : low + high;
    }
    return weights;
  }

  private static long sumDoublePalindromes(int[] weights, int index, int value) {
    if (index == weights.length) {
      var mirrored = Integer.reverse(value) >>> Integer.numberOfLeadingZeros(value);
      return mirrored == value ? value : 0;
    }
    var sum = 0L;
    // Outermost digit: odd, as explained above. Inner digits: anything.
    var first = index == 0 ? 1 : 0;
    var step = index == 0 ? 2 : 1;
    for (var d = first; d < 10; d += step) {
      sum += sumDoublePalindromes(weights, index + 1, value + d * weights[index]);
    }
    return sum;
  }
}
