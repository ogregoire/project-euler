package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Prime permutations.
 *
 * <p>Primes are grouped by their digits, using the sorted digits as the key of a bucket, so a prime
 * is only ever compared to its own permutations instead of to every other prime. Taking each prime
 * c in increasing order as the last term, every smaller prime b of its bucket fixes the first term
 * a = 2b - c, which only needs a lookup. The first sequence found has the smallest last term, so
 * the search stops right there.
 */
public class Problem49 extends AbstractProblem {

  private static final int MIN = 1000;
  private static final int LIMIT = 10000;

  /** The first term of the sequence given in the statement, which must be skipped. */
  private static final int KNOWN_FIRST_TERM = 1487;

  @Override
  public Object solve() {
    var composite = new boolean[LIMIT];
    for (var i = 2; i * i < LIMIT; i++) {
      if (!composite[i]) {
        for (var multiple = i * i; multiple < LIMIT; multiple += i) {
          composite[multiple] = true;
        }
      }
    }

    // Buckets as linked lists: the latest prime per key, then each prime links to the previous.
    var latest = new int[LIMIT];
    var previous = new int[LIMIT];
    for (var c = MIN + 1; c < LIMIT; c += 2) {
      if (composite[c]) {
        continue;
      }
      var key = sortedDigits(c);
      for (var b = latest[key]; b != 0; b = previous[b]) {
        var a = 2 * b - c;
        if (a >= MIN && !composite[a] && a != KNOWN_FIRST_TERM && sortedDigits(a) == key) {
          return "" + a + b + c;
        }
      }
      previous[c] = latest[key];
      latest[key] = c;
    }
    return null;
  }

  /** The four digits of {@code n} sorted in increasing order, read as a number. */
  private static int sortedDigits(int n) {
    var d1 = n / 1000;
    var d2 = n / 100 % 10;
    var d3 = n / 10 % 10;
    var d4 = n % 10;
    // A sorting network: sort both pairs, then their minimums and maximums are the extremes.
    var low1 = Math.min(d1, d2);
    var high1 = Math.max(d1, d2);
    var low2 = Math.min(d3, d4);
    var high2 = Math.max(d3, d4);
    var lowest = Math.min(low1, low2);
    var highest = Math.max(high1, high2);
    var middle1 = Math.max(low1, low2);
    var middle2 = Math.min(high1, high2);
    return ((lowest * 10 + Math.min(middle1, middle2)) * 10 + Math.max(middle1, middle2)) * 10
        + highest;
  }
}
