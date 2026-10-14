package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Non-abundant sums.
 *
 * <p>Instead of generating every sum of two abundant numbers (quadratic), this asks, for each
 * candidate, whether it is such a sum, and uses parity to make that question cheap:
 *
 * <ul>
 *   <li>Even numbers above 46 are always sums of two abundant numbers (proven below), so they need
 *       no search at all.
 *   <li>An odd sum needs exactly one odd abundant number, and those are rare (62 up to the limit,
 *       the first being 945). Odd candidates only scan that short list.
 * </ul>
 */
public class Problem23 extends AbstractProblem {

  /** Given by the problem: every integer above it is a sum of two abundant numbers. */
  private static final int LIMIT = 28123;

  /** Every even number above this one is a sum of two abundant numbers. */
  private static final int LARGEST_EVEN_NON_SUM = 46;

  @Override
  public Object solve() {
    // Sum of proper divisors of every number, sieve-style: each i is a proper divisor of its
    // multiples.
    var divisorSums = new int[LIMIT + 1];
    for (var i = 1; i <= LIMIT / 2; i++) {
      for (var multiple = i + i; multiple <= LIMIT; multiple += i) {
        divisorSums[multiple] += i;
      }
    }

    var isAbundant = new boolean[LIMIT + 1];
    var oddAbundants = new int[LIMIT];
    var oddAbundantCount = 0;
    for (var n = 1; n <= LIMIT; n++) {
      if (divisorSums[n] > n) {
        isAbundant[n] = true;
        if (n % 2 != 0) {
          oddAbundants[oddAbundantCount++] = n;
        }
      }
    }

    var sum = 0;
    for (var n = 1; n <= LIMIT; n++) {
      if (!isSumOfTwoAbundants(n, isAbundant, oddAbundants, oddAbundantCount)) {
        sum += n;
      }
    }
    return sum;
  }

  private static boolean isSumOfTwoAbundants(
      int n, boolean[] isAbundant, int[] oddAbundants, int oddAbundantCount) {
    if (n % 2 == 0) {
      if (n > LARGEST_EVEN_NON_SUM) {
        // Every multiple 6k of 6 with k >= 2 is abundant: 1 + k + 2k + 3k > 6k. And 20 and 40 are
        // abundant. So for even n >= 48, depending on n mod 6, one of these is a valid split:
        //   n = 12 + (n - 12),  n = 20 + (n - 20),  n = 40 + (n - 40)
        // where the second term is a multiple of 6 that is at least 12.
        return true;
      }
      // Few small even numbers left: brute force over every split.
      for (var a = 1; a <= n / 2; a++) {
        if (isAbundant[a] && isAbundant[n - a]) {
          return true;
        }
      }
      return false;
    }
    // Odd = odd + even, so one of the two terms must come from the short list of odd abundants.
    for (var i = 0; i < oddAbundantCount && oddAbundants[i] < n; i++) {
      if (isAbundant[n - oddAbundants[i]]) {
        return true;
      }
    }
    return false;
  }
}
