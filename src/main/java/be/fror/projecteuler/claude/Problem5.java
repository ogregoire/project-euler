package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Smallest multiple.
 *
 * <p>The answer is the least common multiple of 1 to 20: the product, over each prime {@code p} up
 * to 20, of the largest power of {@code p} not above 20. The primes come from a tiny sieve of
 * Eratosthenes, which uses only additions, so the whole computation needs no division.
 */
public class Problem5 extends AbstractProblem {

  private static final int N = 20;

  @Override
  public Object solve() {
    var composite = new boolean[N + 1];
    var lcm = 1L;
    for (var p = 2; p <= N; p++) {
      if (composite[p]) {
        continue;
      }
      var power = p;
      while (power * p <= N) {
        power *= p;
      }
      lcm *= power;
      for (var multiple = p * p; multiple <= N; multiple += p) {
        composite[multiple] = true;
      }
    }
    return lcm;
  }
}
