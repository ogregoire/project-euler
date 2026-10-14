package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;
import java.util.Arrays;

/**
 * Circular primes.
 *
 * <p>Above one digit, a circular prime may only use the digits 1, 3, 7 and 9: any other digit,
 * rotated into the last position, makes a multiple of 2 or 5. So instead of sieving every number
 * below the limit, this only builds the numbers made of those four digits.
 *
 * <p>All rotations of a number are prime or not together, so each cycle of rotations is examined
 * once, from its smallest member, and stops at the first composite rotation. The smallest member
 * starts with its smallest digit, so numbers with a smaller digit than their first are never even
 * built.
 *
 * <p>Primality is tested by trial division with the primes up to the square root of the limit, in
 * two passes: all the rotations are first screened with a few small primes, which rejects most
 * cycles cheaply, and only then are the survivors proven prime with the remaining divisions.
 */
public class Problem35 extends AbstractProblem {

  private static final int LIMIT = 1_000_000;

  private static final int[] ALLOWED_DIGITS = {1, 3, 7, 9};

  /** How many of the smallest primes screen all the rotations before any is proven prime. */
  private static final int SCREENING_PRIMES = 10;

  @Override
  public Object solve() {
    var primes = primesUpTo((int) Math.sqrt(LIMIT));

    var count = 0;
    for (var p : primes) {
      if (p < 10) {
        count++;
      }
    }

    var digitCount = 2;
    for (var power = 10; power * 10 <= LIMIT; power *= 10, digitCount++) {
      for (var first = 0; first < ALLOWED_DIGITS.length; first++) {
        count += countCircularPrimes(ALLOWED_DIGITS[first], digitCount - 1, first, power, primes);
      }
    }
    return count;
  }

  /**
   * Counts the circular primes made of {@code prefix} followed by {@code remaining} digits, none
   * smaller than {@code ALLOWED_DIGITS[smallest]}.
   */
  private static int countCircularPrimes(
      int prefix, int remaining, int smallest, int power, int[] primes) {
    if (remaining == 0) {
      return circularPrimesInCycle(prefix, power, primes);
    }
    var count = 0;
    for (var digit = smallest; digit < ALLOWED_DIGITS.length; digit++) {
      var n = prefix * 10 + ALLOWED_DIGITS[digit];
      count += countCircularPrimes(n, remaining - 1, smallest, power, primes);
    }
    return count;
  }

  /**
   * Returns how many distinct numbers the rotations of {@code n} form if they are all prime and
   * {@code n} is the smallest of them, and 0 otherwise, so that each cycle is counted once.
   */
  private static int circularPrimesInCycle(int n, int power, int[] primes) {
    var distinct = 0;
    var rotation = n;
    do {
      if (rotation < n) {
        return 0;
      }
      distinct++;
      rotation = rotation % 10 * power + rotation / 10;
    } while (rotation != n);

    for (var i = 0; i < distinct; i++) {
      if (!hasNoFactorAmong(rotation, primes, 0, SCREENING_PRIMES)) {
        return 0;
      }
      rotation = rotation % 10 * power + rotation / 10;
    }
    for (var i = 0; i < distinct; i++) {
      if (!hasNoFactorAmong(rotation, primes, SCREENING_PRIMES, primes.length)) {
        return 0;
      }
      rotation = rotation % 10 * power + rotation / 10;
    }
    return distinct;
  }

  /**
   * Returns whether {@code n} has no prime factor other than itself among {@code primes[from]} to
   * {@code primes[to - 1]}. Given all the primes up to its square root, that means it is prime.
   */
  private static boolean hasNoFactorAmong(int n, int[] primes, int from, int to) {
    for (var i = from; i < to; i++) {
      var p = primes[i];
      if (p * p > n) {
        return true;
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
    for (var i = 2; i <= limit; i++) {
      if (!composite[i]) {
        primes[count++] = i;
        for (var j = i * i; j <= limit; j += i) {
          composite[j] = true;
        }
      }
    }
    return Arrays.copyOf(primes, count);
  }
}
