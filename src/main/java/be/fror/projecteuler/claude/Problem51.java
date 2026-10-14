package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Prime digit replacements.
 *
 * <p>Primes are tried in increasing order, one digit count at a time, with a sieve sized for that
 * digit count. The first prime that is the smallest member of an eight prime family is the answer,
 * since the smallest member of any family is itself in that family. That keeps the search small:
 *
 * <ul>
 *   <li>The smallest member holds the lowest of the replacement digits, and at most two of the ten
 *       digits may fail, so only primes holding a 0, 1 or 2 are tried, and only with larger
 *       replacement digits.
 *   <li>The units digit is never replaced: half of the replacements would be even.
 *   <li>For a family of more than seven, the number of replaced digits is a multiple of 3.
 *       Otherwise, replacing them by 0 to 9 makes the digit sum take every residue modulo 3, so at
 *       least three of the ten numbers are divisible by 3.
 * </ul>
 *
 * <p>Replacing digits is plain arithmetic: changing the replaced digits from {@code d} to {@code r}
 * adds {@code (r - d)} times the sum of the replaced positions' powers of ten.
 */
public class Problem51 extends AbstractProblem {

  private static final int FAMILY_SIZE = 8;

  @Override
  public Object solve() {
    return smallestPrimeInFamily(FAMILY_SIZE);
  }

  static int smallestPrimeInFamily(int familySize) {
    for (var digitCount = 1; ; digitCount++) {
      var limit = pow10(digitCount);
      var isComposite = sieve(limit);
      for (var p = Math.max(2, limit / 10); p < limit; p++) {
        if (!isComposite[p] && isSmallestInFamily(p, digitCount, familySize, isComposite)) {
          return p;
        }
      }
    }
  }

  private static boolean isSmallestInFamily(
      int p, int digitCount, int familySize, boolean[] isComposite) {
    for (var digit = 0; digit <= 10 - familySize; digit++) {
      // Bit i set when the digit at 10^i is `digit`, units excluded.
      var positions = 0;
      var rest = p / 10;
      for (var i = 1; i < digitCount; i++, rest /= 10) {
        if (rest % 10 == digit) {
          positions |= 1 << i;
        }
      }
      // Every non-empty subset of those positions.
      for (var subset = positions; subset != 0; subset = (subset - 1) & positions) {
        if (familySize > 7 && Integer.bitCount(subset) % 3 != 0) {
          continue;
        }
        var step = 0;
        for (var i = 1; i < digitCount; i++) {
          if ((subset & (1 << i)) != 0) {
            step += pow10(i);
          }
        }
        if (countPrimes(p, digit, step, familySize, isComposite) >= familySize) {
          return true;
        }
      }
    }
    return false;
  }

  /** Counts the primes in the family of {@code p}, stopping once it cannot reach the size. */
  private static int countPrimes(
      int p, int digit, int step, int familySize, boolean[] isComposite) {
    var primes = 1;
    var allowedFailures = 9 - digit - (familySize - 1);
    for (var replacement = digit + 1; replacement <= 9; replacement++) {
      if (!isComposite[p + (replacement - digit) * step]) {
        primes++;
      } else if (--allowedFailures < 0) {
        break;
      }
    }
    return primes;
  }

  private static boolean[] sieve(int limit) {
    var isComposite = new boolean[limit];
    isComposite[0] = true;
    isComposite[1] = true;
    for (var i = 2; i * i < limit; i++) {
      if (!isComposite[i]) {
        for (var multiple = i * i; multiple < limit; multiple += i) {
          isComposite[multiple] = true;
        }
      }
    }
    return isComposite;
  }

  private static int pow10(int exponent) {
    var result = 1;
    for (var i = 0; i < exponent; i++) {
      result *= 10;
    }
    return result;
  }
}
