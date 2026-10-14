package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Pentagon numbers.
 *
 * <p>Walks the candidate differences D = P(i) in increasing order, so the first D that works is the
 * minimum. For a given D, every pair P(k) - P(j) = D with k = j + g satisfies
 * {@code 2D = g (6j + 3g - 1)}: the gap g is a divisor of 2D = i (3i - 1), and each divisor gives j
 * directly. This replaces a scan over every possible gap (about i divisions per D) with the few
 * dozen divisors of 2D.
 *
 * <p>The divisors are generated from the prime factorizations of i and 3i - 1 (coprime, so their
 * factors combine without merging), read off a smallest-prime-factor table that grows as needed.
 */
public class Problem44 extends AbstractProblem {

  @Override
  public Object solve() {
    var smallestFactors = smallestPrimeFactors(1 << 12);
    // A number N has at most 2 sqrt(N) divisors, and 2D = i (3i - 1) < m * m.
    var divisors = new long[2 * smallestFactors.length];
    for (var i = 1; ; i++) {
      var m = 3 * i - 1;
      if (m >= smallestFactors.length) {
        smallestFactors = smallestPrimeFactors(2 * smallestFactors.length);
        divisors = new long[2 * smallestFactors.length];
      }
      divisors[0] = 1;
      var count = addDivisors(i, smallestFactors, divisors, 1);
      count = addDivisors(m, smallestFactors, divisors, count);
      for (var k = 0; k < count; k++) {
        var gap = divisors[k];
        // The list is symmetric: the k-th divisor from each end multiply to 2D.
        var twiceDOverGap = divisors[count - 1 - k];
        // From 2D = g (6j + 3g - 1): j is a positive integer only for this residue and sign.
        var sixJ = twiceDOverGap - 3 * gap + 1;
        if (sixJ > 0 && sixJ % 6 == 0) {
          var j = sixJ / 6;
          if (isPentagonal(pentagonal(j) + pentagonal(j + gap))) {
            return pentagonal(i);
          }
        }
      }
    }
  }

  /**
   * Multiplies the first {@code count} divisors by every power of each prime factor of {@code n},
   * appending the results, and returns the new count. Each prime power block is a scaled copy of
   * the symmetric list before it, so the list stays symmetric.
   */
  private static int addDivisors(int n, int[] smallestFactors, long[] divisors, int count) {
    while (n > 1) {
      var p = smallestFactors[n];
      var size = count;
      var power = 1L;
      while (n % p == 0) {
        n /= p;
        power *= p;
        for (var k = 0; k < size; k++) {
          divisors[count++] = divisors[k] * power;
        }
      }
    }
    return count;
  }

  /** Smallest prime factor of every number below {@code size}. */
  private static int[] smallestPrimeFactors(int size) {
    var factors = new int[size];
    for (var n = 2; n < size; n++) {
      if (factors[n] == 0) {
        for (var multiple = n; multiple < size; multiple += n) {
          if (factors[multiple] == 0) {
            factors[multiple] = n;
          }
        }
      }
    }
    return factors;
  }

  private static long pentagonal(long n) {
    return n * (3 * n - 1) / 2;
  }

  /** Inverts {@code x = n (3n - 1) / 2}: n = (1 + s) / 6 with s the square root of 24x + 1. */
  private static boolean isPentagonal(long x) {
    var v = 24 * x + 1;
    var s = (long) Math.sqrt(v);
    return s * s == v && s % 6 == 5;
  }
}
