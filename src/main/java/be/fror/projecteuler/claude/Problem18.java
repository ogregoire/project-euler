package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Maximum path sum I.
 *
 * <p>Folds the triangle from the bottom up: the best path from a cell is the cell plus the better
 * of the best paths from its two children. Only one row of best sums is kept, overwritten in
 * place, so the given triangle is never copied or modified.
 */
public class Problem18 extends AbstractProblem {

  private static final int[][] TRIANGLE = {
    {75},
    {95, 64},
    {17, 47, 82},
    {18, 35, 87, 10},
    {20, 4, 82, 47, 65},
    {19, 1, 23, 75, 3, 34},
    {88, 2, 77, 73, 7, 63, 67},
    {99, 65, 4, 28, 6, 16, 70, 92},
    {41, 41, 26, 56, 83, 40, 80, 70, 33},
    {41, 48, 72, 33, 47, 32, 37, 16, 94, 29},
    {53, 71, 44, 65, 25, 43, 91, 52, 97, 51, 14},
    {70, 11, 33, 28, 77, 73, 17, 78, 39, 68, 17, 57},
    {91, 71, 52, 38, 17, 14, 91, 43, 58, 50, 27, 29, 48},
    {63, 66, 4, 68, 89, 53, 67, 30, 73, 16, 69, 87, 40, 31},
    {4, 62, 98, 27, 23, 9, 70, 98, 73, 93, 38, 53, 60, 4, 23}
  };

  @Override
  public Object solve() {
    var best = TRIANGLE[TRIANGLE.length - 1].clone();
    for (var i = TRIANGLE.length - 2; i >= 0; i--) {
      var row = TRIANGLE[i];
      // Left to right, best[j + 1] is still the row below when best[j] is overwritten.
      for (var j = 0; j < row.length; j++) {
        best[j] = row[j] + Math.max(best[j], best[j + 1]);
      }
    }
    return best[0];
  }
}
