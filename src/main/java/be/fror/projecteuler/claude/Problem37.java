package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Truncatable primes.
 *
 * <p>Instead of sieving and testing every prime up to an arbitrary limit, this grows the primes
 * that stay prime when truncated from the right: they are exactly the single-digit primes,
 * extended one digit at a time with 1, 3, 7 or 9 as long as the result is prime. That tree is
 * finite and tiny (83 primes), so it is explored completely, which also proves that no truncatable
 * prime is missed. Only its members then need the left-truncation check. Primality uses a
 * deterministic Miller-Rabin test, which is cheap for these few numbers.
 */
public class Problem37 extends AbstractProblem {

  private static final int[] SINGLE_DIGIT_PRIMES = {2, 3, 5, 7};

  /** Any other last digit would make the number even or a multiple of 5. */
  private static final int[] APPENDABLE_DIGITS = {1, 3, 7, 9};

  /** Trial division by these rejects most composites before the costlier Miller-Rabin test. */
  private static final int[] SMALL_PRIMES = {2, 3, 5, 7};

  /** Enough witnesses for Miller-Rabin to be exact below 4,759,123,141, so for any int. */
  private static final int[] WITNESSES = {2, 7, 61};

  @Override
  public Object solve() {
    var sum = 0;
    for (var prime : SINGLE_DIGIT_PRIMES) {
      sum += sumOfTruncatables(prime);
    }
    return sum;
  }

  /** Sums the two-sided truncatable primes among {@code prime} and its right extensions. */
  private static int sumOfTruncatables(int prime) {
    // Single-digit primes are not considered truncatable.
    var sum = prime >= 10 && isLeftTruncatable(prime) ? prime : 0;
    for (var digit : APPENDABLE_DIGITS) {
      var extended = prime * 10 + digit;
      if (isPrime(extended)) {
        sum += sumOfTruncatables(extended);
      }
    }
    return sum;
  }

  private static boolean isLeftTruncatable(int prime) {
    for (var power = 10; power < prime; power *= 10) {
      if (!isPrime(prime % power)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isPrime(int n) {
    for (var p : SMALL_PRIMES) {
      if (n % p == 0) {
        return n == p;
      }
    }
    // Without a factor below 11, any composite is at least 11².
    if (n < 121) {
      return n > 1;
    }
    for (var witness : WITNESSES) {
      if (!isStrongProbablePrime(n, witness)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isStrongProbablePrime(int n, int witness) {
    var shift = Integer.numberOfTrailingZeros(n - 1);
    var x = powMod(witness, (n - 1) >>> shift, n);
    if (x == 1 || x == n - 1) {
      return true;
    }
    for (var i = 1; i < shift; i++) {
      x = x * x % n;
      if (x == n - 1) {
        return true;
      }
    }
    return false;
  }

  private static long powMod(long base, int exponent, int modulus) {
    var result = 1L;
    for (; exponent > 0; exponent >>>= 1) {
      if ((exponent & 1) != 0) {
        result = result * base % modulus;
      }
      base = base * base % modulus;
    }
    return result;
  }
}
