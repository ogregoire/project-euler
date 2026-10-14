package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Number spiral diagonals.
 *
 * <p>The ring of side {@code s = 2k + 1} has its corners at {@code s²}, {@code s² - (s - 1)},
 * {@code s² - 2(s - 1)} and {@code s² - 3(s - 1)}, which add up to {@code 4s² - 6s + 6}. Summing
 * that over {@code k = 1..r} gives a closed form, so no loop is needed.
 */
public class Problem28 extends AbstractProblem {

  private static final int SIDE = 1001;

  @Override
  public Object solve() {
    long rings = (SIDE - 1) / 2;
    return 1 + (16 * rings * rings * rings + 30 * rings * rings + 26 * rings) / 3;
  }
}
