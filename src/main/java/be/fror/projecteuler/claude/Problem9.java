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

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int perimeter = 1000;

  @Override
  public Object solve() {
    return tripletProduct(perimeter);
  }

  private static long tripletProduct(int perimeter) {
    // 3 + 4 + 5 is the smallest perimeter of a Pythagorean triplet.
    if (perimeter < 12) {
      throw new IllegalArgumentException("perimeter must be at least 12, got " + perimeter);
    }
    // Every triplet is k (m^2 - n^2, 2mn, m^2 + n^2) with m > n >= 1, so its perimeter is
    // 2k m (m + n): always even.
    if (perimeter % 2 != 0) {
      throw new IllegalStateException("No Pythagorean triplet has perimeter " + perimeter);
    }
    var half = perimeter / 2;
    for (var m = 2; m * m < half; m++) {
      for (var n = 1; n < m; n++) {
        var primitiveHalfPerimeter = m * (m + n);
        if (half % primitiveHalfPerimeter == 0) {
          var k = half / primitiveHalfPerimeter;
          // long, because a * b * c overflows an int for perimeters above about 3900.
          var a = (long) (m * m - n * n) * k;
          var b = 2L * m * n * k;
          var c = (long) (m * m + n * n) * k;
          return a * b * c;
        }
      }
    }
    throw new IllegalStateException("No Pythagorean triplet has perimeter " + perimeter);
  }
}
