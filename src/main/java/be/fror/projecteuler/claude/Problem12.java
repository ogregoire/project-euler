package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Highly divisible triangular number.
 *
 * <p>T(n) = n(n + 1) / 2, and n and n + 1 are coprime. So T(n) splits into two coprime factors,
 * n / 2 and n + 1 or n and (n + 1) / 2 depending on parity, and its divisor count is the product
 * of theirs. Those small factors get their divisor counts from a linear sieve, which costs a few
 * operations per number instead of a factorization each. As the needed size isn't known upfront,
 * the sieve doubles until it is large enough.
 */
public class Problem12 extends AbstractProblem {

  private static final int DIVISORS = 500;

  @Override
  public Object solve() {
    for (var limit = 1024; ; limit *= 2) {
      var divisorCounts = divisorCounts(limit);
      for (var n = 1; n < limit; n++) {
        var count = n % 2 == 0
            ? divisorCounts[n / 2] * divisorCounts[n + 1]
            : divisorCounts[n] * divisorCounts[(n + 1) / 2];
        if (count > DIVISORS) {
          return n * (n + 1) / 2;
        }
      }
    }
  }

  /** The number of divisors of every integer from 1 to {@code limit}, with a linear sieve. */
  private static int[] divisorCounts(int limit) {
    var counts = new int[limit + 1];
    // The exponent of the smallest prime factor, to update counts when that prime is added again.
    var exponents = new int[limit + 1];
    var primes = new int[limit + 1];
    var primeCount = 0;
    counts[1] = 1;
    for (var i = 2; i <= limit; i++) {
      if (counts[i] == 0) {
        counts[i] = 2;
        exponents[i] = 1;
        primes[primeCount++] = i;
      }
      // Each composite is reached once, as i times its smallest prime factor p.
      for (var k = 0; k < primeCount; k++) {
        var p = primes[k];
        var multiple = i * p;
        if (multiple > limit) {
          break;
        }
        if (i % p == 0) {
          // The exponent e of p grows by one: the (e + 1) factor of the count becomes (e + 2).
          exponents[multiple] = exponents[i] + 1;
          counts[multiple] = counts[i] / (exponents[i] + 1) * (exponents[i] + 2);
          break;
        }
        exponents[multiple] = 1;
        counts[multiple] = counts[i] * 2;
      }
    }
    return counts;
  }
}
