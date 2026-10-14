package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Triangular, pentagonal, and hexagonal.
 *
 * <p>Every hexagonal number is triangular ({@code H(n) = T(2n - 1)}), so only {@code P(k) = H(n)}
 * matters. Multiplying {@code k(3k - 1) / 2 = n(2n - 1)} by 12 and completing both squares turns it
 * into the Pell-type equation {@code x² - 3y² = -2} with {@code x = 6k - 1} and {@code y = 4n - 1}.
 * All its positive solutions come from {@code (1, 1)} by repeatedly multiplying by the fundamental
 * unit {@code 2 + √3}, which grows them geometrically: only a handful are visited, keeping those
 * where {@code k} and {@code n} are integers, instead of walking tens of thousands of numbers.
 */
public class Problem45 extends AbstractProblem {

  /** Given by the problem: H(143) is the second match, after H(1); the third is wanted. */
  private static final int WANTED = 3;

  @Override
  public Object solve() {
    var x = 1L;
    var y = 1L;
    var found = 0;
    while (true) {
      if (x % 6 == 5 && y % 4 == 3 && ++found == WANTED) {
        var n = (y + 1) / 4;
        return n * (2 * n - 1);
      }
      var nextX = 2 * x + 3 * y;
      y = x + 2 * y;
      x = nextX;
    }
  }
}
