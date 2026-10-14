package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Quadratic primes.
 *
 * <p>{@code n = 0} and {@code n = 1} must give primes, so {@code b} is a prime and
 * {@code a = p - b - 1} for a prime {@code p}. Pairing primes leaves a few tens of thousands of
 * candidates instead of about two million. Almost every candidate then fails within the next couple
 * of values of {@code n}, while the values are still small, so primality is mostly a lookup in a
 * small sieve; only the rare long runs fall back to trial division.
 */
public class Problem27 extends AbstractProblem {

  /** Given by the problem: {@code |a| < LIMIT} and {@code |b| <= LIMIT}. */
  private static final int LIMIT = 1000;

  /**
   * Covers {@code n² + an + b} up to {@code n = 2}, and its primes are enough to trial-divide any
   * value up to its square.
   */
  private static final int SIEVE_LIMIT = 4 * LIMIT;

  @Override
  public Object solve() {
    var isComposite = new boolean[SIEVE_LIMIT + 1];
    var primes = new int[SIEVE_LIMIT];
    var primeCount = 0;
    for (var i = 2; i <= SIEVE_LIMIT; i++) {
      if (!isComposite[i]) {
        primes[primeCount++] = i;
        for (var multiple = i * i; multiple <= SIEVE_LIMIT; multiple += i) {
          isComposite[multiple] = true;
        }
      }
    }

    var bestLength = 0;
    var bestProduct = 0;
    for (var bIndex = 0; bIndex < primeCount && primes[bIndex] <= LIMIT; bIndex++) {
      var b = primes[bIndex];
      for (var pIndex = 0; pIndex < primeCount; pIndex++) {
        var a = primes[pIndex] - b - 1;
        if (a <= -LIMIT) {
          continue;
        }
        if (a >= LIMIT) {
          break;
        }
        // n = 0 and n = 1 give b and p, both prime.
        var length = 2;
        while (isPrime(length * (length + a) + b, isComposite, primes)) {
          length++;
        }
        if (length > bestLength) {
          bestLength = length;
          bestProduct = a * b;
        }
      }
    }
    return bestProduct;
  }

  private static boolean isPrime(int value, boolean[] isComposite, int[] primes) {
    if (value < 2) {
      return false;
    }
    if (value < isComposite.length) {
      return !isComposite[value];
    }
    // Never runs out of primes: a run stops at n = b at the latest, since n² + an + b is then
    // b (b + a + 1), so values stay below 3 LIMIT², within SIEVE_LIMIT².
    for (var i = 0; primes[i] * primes[i] <= value; i++) {
      if (value % primes[i] == 0) {
        return false;
      }
    }
    return true;
  }
}
