package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;
import java.util.Arrays;

/**
 * Distinct primes factors.
 *
 * <p>A sieve counts the distinct prime factors of every number, then the counts are scanned for 4
 * consecutive numbers with at least 4. Two things keep the sieve small:
 *
 * <ul>
 *   <li>A number below {@code n} with 4 distinct prime factors has all of them below {@code n /
 *       30}, since the other three multiply to at least 2 * 3 * 5. Larger primes can't make a
 *       count reach 4, so they are skipped.
 *   <li>There is no guessed upper bound: the sieve starts small and doubles until the answer shows
 *       up, and each doubling only sieves the new half, using primes already found in the old one.
 *       So the total work stays proportional to the answer.
 * </ul>
 */
public class Problem47 extends AbstractProblem {

  private static final int FACTORS = 4;

  /** The product of the smallest {@code FACTORS - 1} primes. */
  private static final int SMALLEST_COFACTOR = 2 * 3 * 5;

  @Override
  public Object solve() {
    // At least 900 entries: see sieve.
    var counts = new byte[1 << 10];
    var from = 0;
    var run = 0;
    while (true) {
      sieve(counts, from);
      for (var n = from; n < counts.length; n++) {
        run = counts[n] >= FACTORS ? run + 1 : 0;
        if (run == FACTORS) {
          return n - FACTORS + 1;
        }
      }
      from = counts.length;
      counts = Arrays.copyOf(counts, 2 * counts.length);
    }
  }

  /**
   * Counts the distinct prime factors of the numbers from {@code from} to the end of {@code
   * counts}, as far as needed to tell which ones have at least {@code FACTORS}. The counts below
   * {@code from} must already be done, and {@code from} must be at least half the length.
   */
  private static void sieve(byte[] counts, int from) {
    var maxPrime = counts.length / SMALLEST_COFACTOR;
    for (var p = 2; p <= maxPrime; p++) {
      // Every number up to maxPrime was already sieved by all the primes up to its square root
      // (maxPrime exceeds the square root of the length once that is 900 or more), so a number
      // without any prime factor found yet is a prime.
      if (counts[p] != 0) {
        continue;
      }
      var first = Math.max(2 * p, (from + p - 1) / p * p);
      for (var multiple = first; multiple < counts.length; multiple += p) {
        counts[multiple]++;
      }
    }
  }
}
