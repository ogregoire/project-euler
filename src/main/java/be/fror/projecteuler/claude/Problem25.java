package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * 1000-digit Fibonacci number.
 *
 * <p>No big numbers needed: by Binet's formula, F(n) is the integer nearest to φⁿ / √5, so it has
 * at least {@code DIGITS} digits as soon as {@code n log10(φ) - log10(√5) >= DIGITS - 1}. Solving
 * for n gives the answer directly.
 */
public class Problem25 extends AbstractProblem {

  private static final int DIGITS = 1000;

  @Override
  public Object solve() {
    var sqrt5 = Math.sqrt(5);
    var log10Phi = Math.log10((1 + sqrt5) / 2);
    return (int) Math.ceil((DIGITS - 1 + Math.log10(sqrt5)) / log10Phi);
  }
}
