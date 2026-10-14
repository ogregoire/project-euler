package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Digit fifth powers.
 *
 * <p>A number that equals the sum of the fifth powers of its digits has at most six digits, since
 * seven nines only sum to 413343. The sum depends only on the multiset of digits, so instead of
 * testing all numbers up to a million, this enumerates the 5005 multisets of six digits (zeros
 * standing in for missing leading digits), computes each power sum once, and checks whether the
 * sum's own six digits form the same multiset.
 *
 * <p>A multiset is encoded as a digit-count signature: 3 bits per digit, enough for counts up to
 * six. Comparing two multisets is then a single integer comparison. The signature of a sum is
 * read from a table of all 3-digit groups, so each check costs one division instead of six.
 */
public class Problem30 extends AbstractProblem {

  private static final int POWER = 5;
  private static final int DIGITS = 6;
  private static final int GROUP = 1000;

  @Override
  public Object solve() {
    var powers = new int[10];
    for (var d = 0; d < 10; d++) {
      var p = 1;
      for (var i = 0; i < POWER; i++) {
        p *= d;
      }
      powers[d] = p;
    }
    // Signature of every 3-digit group, leading zeros included.
    var groupSignatures = new int[GROUP];
    var n = 0;
    for (var a = 0; a < 10; a++) {
      for (var b = 0; b < 10; b++) {
        for (var c = 0; c < 10; c++) {
          groupSignatures[n++] = digitBit(a) + digitBit(b) + digitBit(c);
        }
      }
    }
    return sumMatches(powers, groupSignatures, 0, DIGITS, 0, 0);
  }

  /** Picks the remaining digits in non-decreasing order so each multiset is visited once. */
  private static long sumMatches(
      int[] powers, int[] groupSignatures, int minDigit, int left, int sum, int signature) {
    if (left == 0) {
      var sumSignature = groupSignatures[sum / GROUP] + groupSignatures[sum % GROUP];
      // Single-digit "sums" such as 1 = 1^5 are excluded by the problem.
      return sum >= 10 && sumSignature == signature ? sum : 0;
    }
    var total = 0L;
    for (var d = minDigit; d < 10; d++) {
      total +=
          sumMatches(
              powers, groupSignatures, d, left - 1, sum + powers[d], signature + digitBit(d));
    }
    return total;
  }

  private static int digitBit(int digit) {
    return 1 << (3 * digit);
  }
}
