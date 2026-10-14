package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Special Pythagorean triplet.
 *
 * <p>Every Pythagorean triplet is a multiple {@code k} of a primitive one given by Euclid's formula
 * {@code (m² - n², 2mn, m² + n²)} with {@code m > n}. Its perimeter is {@code 2m(m + n)k}, so the
 * search only needs the few pairs {@code (m, n)} where {@code 2m(m + n)} divides the target
 * perimeter, instead of scanning every {@code (a, b)}.
 */
public class Problem9 extends AbstractProblem {

  private static final int PERIMETER = 1000;

  @Override
  public Object solve() {
    var half = PERIMETER / 2;
    for (var m = 2; m * m < half; m++) {
      for (var n = 1; n < m; n++) {
        var primitiveHalfPerimeter = m * (m + n);
        if (half % primitiveHalfPerimeter == 0) {
          var k = half / primitiveHalfPerimeter;
          var a = (m * m - n * n) * k;
          var b = 2 * m * n * k;
          var c = (m * m + n * n) * k;
          return a * b * c;
        }
      }
    }
    throw new AssertionError("No Pythagorean triplet has perimeter " + PERIMETER);
  }
}
