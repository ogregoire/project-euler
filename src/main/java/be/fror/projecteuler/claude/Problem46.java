package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Goldbach's other conjecture.
 *
 * <p>A sieve answers "is it prime?" with one array read, so testing an odd composite {@code n}
 * costs one read per twice-square {@code 2k² < n}, and most candidates succeed within the first
 * few values of {@code k}. The answer's size is unknown, so the sieve starts small and doubles
 * until a counterexample shows up; candidates already tested are not tested again.
 */
public class Problem46 extends AbstractProblem {

  private static final int INITIAL_LIMIT = 1024;

  @Override
  public Object solve() {
    var n = 9;
    for (var limit = INITIAL_LIMIT; ; limit *= 2) {
      var notPrime = sieve(limit);
      for (; n < limit; n += 2) {
        if (notPrime[n] && !isPrimePlusTwiceSquare(n, notPrime)) {
          return n;
        }
      }
    }
  }

  private static boolean isPrimePlusTwiceSquare(int n, boolean[] notPrime) {
    for (var k = 1; 2 * k * k < n; k++) {
      if (!notPrime[n - 2 * k * k]) {
        return true;
      }
    }
    return false;
  }

  /** Sieve of Eratosthenes over odd numbers only, since only odd numbers are ever looked up. */
  private static boolean[] sieve(int limit) {
    var notPrime = new boolean[limit];
    notPrime[1] = true;
    for (var p = 3; p * p < limit; p += 2) {
      if (!notPrime[p]) {
        for (var multiple = p * p; multiple < limit; multiple += 2 * p) {
          notPrime[multiple] = true;
        }
      }
    }
    return notPrime;
  }
}
