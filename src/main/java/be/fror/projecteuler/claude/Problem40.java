package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Champernowne's constant.
 *
 * <p>Each digit is located arithmetically: skip whole blocks of same-length numbers (9 one-digit
 * numbers, 90 two-digit ones, ...), then the offset inside the block gives the number and the
 * digit within it. Digits are extracted by division, so no string is ever built.
 */
public class Problem40 extends AbstractProblem {

  private static final int LAST_POSITION = 1_000_000;

  @Override
  public Object solve() {
    var product = 1;
    for (var position = 1; position <= LAST_POSITION; position *= 10) {
      product *= digitAt(position);
    }
    return product;
  }

  /** Returns the digit at the given 1-based position of the fractional part. */
  private static int digitAt(int position) {
    var offset = position - 1;
    var length = 1;
    var blockSize = 9;
    var firstNumber = 1;
    while (offset >= length * blockSize) {
      offset -= length * blockSize;
      length++;
      blockSize *= 10;
      firstNumber *= 10;
    }
    var number = firstNumber + offset / length;
    for (var i = offset % length + 1; i < length; i++) {
      number /= 10;
    }
    return number % 10;
  }
}
