package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Pandigital products.
 *
 * <p>Nine digits split as multiplicand, multiplier and product only fit in two shapes: 1 digit
 * times 4 digits, or 2 digits times 3 digits, both giving a 4-digit product. So it is enough to
 * walk those factor pairs while the product stays below 10000.
 *
 * <p>A table maps every number below 10000 to the bit set of its digits, or to "invalid" when a
 * digit is 0 or repeated. Checking a pair then costs three lookups and a few bitwise operations,
 * and most pairs are rejected by the first one.
 */
public class Problem32 extends AbstractProblem {

  private static final int PRODUCT_LIMIT = 10_000;

  /** Bit set of the digits 0 to 9: bit 0 is always set, so a 0 digit counts as a repeat. */
  private static final int ALL_DIGITS = (1 << 10) - 1;

  /** All bits set: it overlaps every digit, so an invalid number fails the disjointness test. */
  private static final int INVALID = -1;

  @Override
  public Object solve() {
    var digitSets = new int[PRODUCT_LIMIT];
    digitSets[0] = 1;
    for (var n = 1; n < PRODUCT_LIMIT; n++) {
      var prefix = digitSets[n / 10];
      var digit = 1 << (n % 10);
      digitSets[n] = (prefix & digit) != 0 ? INVALID : prefix | digit;
    }

    // Some products have several factorizations, but they must be counted once.
    var counted = new boolean[PRODUCT_LIMIT];
    var sum = 0;
    for (var a = 2; a < 100; a++) {
      var aDigits = digitSets[a];
      if (aDigits == INVALID) {
        continue;
      }
      for (var b = a < 10 ? 1234 : 123; a * b < PRODUCT_LIMIT; b++) {
        var bDigits = digitSets[b];
        if ((aDigits & bDigits) != 1) {
          continue;
        }
        var product = a * b;
        var productDigits = digitSets[product];
        var factorDigits = aDigits | bDigits;
        if ((factorDigits & productDigits) == 1
            && (factorDigits | productDigits) == ALL_DIGITS
            && !counted[product]) {
          counted[product] = true;
          sum += product;
        }
      }
    }
    return sum;
  }
}
