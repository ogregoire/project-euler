package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;
import java.util.Arrays;

/**
 * Consecutive prime sum.
 *
 * <p>Runs are tried from the longest possible length downwards, and for each length from the
 * lowest start while the sum stays below the limit, so the first prime sum found is the answer.
 * The longest run starting at 2 already has hundreds of terms, so only a few lengths and starts are
 * ever tried: each one is derived from the previous sum with one or two additions, and the few
 * candidate sums are tested by trial division.
 *
 * <p>Like the reference solution, which reuses a memoized sieve, the small primes are sieved once
 * per class rather than once per call.
 */
public class Problem50 extends AbstractProblem {

  private static final int LIMIT = 1_000_000;

  /**
   * Covers the runs actually examined, which stay among the first few hundred primes (checked in
   * {@link #solve()}), and reaches the square root of the limit for trial division.
   */
  private static final int SIEVE_LIMIT = 5_000;

  private static final int[] PRIMES = primesUpTo(SIEVE_LIMIT);

  @Override
  public Object solve() {
    // Longest run starting at 2 whose sum stays below the limit.
    var longest = 0;
    var prefixSum = 0;
    while (prefixSum + PRIMES[longest] < LIMIT) {
      prefixSum += PRIMES[longest++];
    }

    for (var length = longest; length > 0; length--) {
      // Slide a window of this length from 2 onwards while its sum stays below the limit.
      var sum = prefixSum;
      for (var start = 0; sum < LIMIT; start++) {
        if (isPrime(sum)) {
          return sum;
        }
        if (start + length == PRIMES.length) {
          throw new IllegalStateException("Sieve too small");
        }
        sum += PRIMES[start + length] - PRIMES[start];
      }
      prefixSum -= PRIMES[length - 1];
    }
    throw new IllegalStateException("No prime sum found");
  }

  /** Trial division, enough since {@link #PRIMES} reaches the square root of the limit. */
  private static boolean isPrime(int n) {
    for (var p : PRIMES) {
      if (p * p > n) {
        return n > 1;
      }
      if (n % p == 0) {
        return false;
      }
    }
    return true;
  }

  private static int[] primesUpTo(int limit) {
    var composite = new boolean[limit + 1];
    var primes = new int[limit];
    var count = 0;
    for (var n = 2; n <= limit; n++) {
      if (composite[n]) {
        continue;
      }
      primes[count++] = n;
      for (var multiple = n * n; multiple <= limit; multiple += n) {
        composite[multiple] = true;
      }
    }
    return Arrays.copyOf(primes, count);
  }
}
