package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Largest prime factor.
 *
 * <p>Plain trial division that divides each factor out as soon as it is found. The remaining
 * cofactor shrinks with every factor, so the search stops once the divisor squared exceeds it,
 * long before the square root of the original number. No prime sieve is needed: composite
 * divisors never divide the cofactor because their prime factors were already removed.
 */
public class Problem3 extends AbstractProblem {

  private static final long NUMBER = 600851475143L;

  @Override
  public Object solve() {
    var n = NUMBER;
    while (n % 2 == 0 && n > 2) {
      n /= 2;
    }
    for (var divisor = 3L; divisor * divisor <= n; divisor += 2) {
      while (n % divisor == 0 && n > divisor) {
        n /= divisor;
      }
    }
    // What is left has no divisor up to its square root: it is the largest prime factor.
    return n;
  }
}
