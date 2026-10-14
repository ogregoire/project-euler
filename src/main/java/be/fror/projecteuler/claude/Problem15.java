package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Lattice paths.
 *
 * <p>A path is a sequence of {@code 2n} moves of which {@code n} go right, so there are
 * {@code C(2n, n)} of them. Building it as {@code C(n + i, i) = C(n + i - 1, i - 1) * (n + i) / i}
 * keeps every intermediate value an exact binomial coefficient, so the division is always exact
 * and everything fits in a {@code long}: no factorials and no {@code BigInteger}.
 */
public class Problem15 extends AbstractProblem {

  private static final int GRID_SIZE = 20;

  @Override
  public Object solve() {
    var paths = 1L;
    for (var i = 1; i <= GRID_SIZE; i++) {
      paths = paths * (GRID_SIZE + i) / i;
    }
    return paths;
  }
}
