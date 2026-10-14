package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Pandigital multiples.
 *
 * <p>The example 918273645 (9 times 1 to 5) is a lower bound, so the answer starts with 9 and so
 * does n. Counting digits rules out every shape except a 4-digit n concatenated with 2n: a 2-digit
 * 9x gives 2 + 3 + 3 digits then jumps past 9, and a 3-digit 9xx gives 3 + 4 then jumps past 9.
 *
 * <p>Moreover 2n must start with 18: from n = 9500 up it starts with 19, repeating the 9 of n. So
 * the search walks n down from 9499 and the first pandigital concatenation is the largest. Each
 * candidate is rejected at its first repeated digit.
 */
public class Problem38 extends AbstractProblem {

  @Override
  public Object solve() {
    for (var n = 9499; n >= 9000; n--) {
      var concatenation = n * 100_000 + 2 * n;
      if (hasDistinctNonZeroDigits(concatenation)) {
        // Nine distinct digits other than 0 are exactly 1 to 9.
        return concatenation;
      }
    }
    throw new IllegalStateException("No pandigital multiple found");
  }

  private static boolean hasDistinctNonZeroDigits(int n) {
    // Bit 0 starts set so that a 0 digit is rejected as a repeat.
    var seen = 1;
    for (var x = n; x > 0; x /= 10) {
      var digit = 1 << (x % 10);
      if ((seen & digit) != 0) {
        return false;
      }
      seen |= digit;
    }
    return true;
  }
}
