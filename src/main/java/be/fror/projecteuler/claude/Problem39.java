package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Integer right triangles.
 *
 * <p>Every right triangle with integral sides is a multiple {@code k} of a primitive one, which
 * Euclid's formula generates exactly once from coprime {@code m > n} of opposite parity:
 * {@code (m² - n², 2mn, m² + n²)}. Its perimeter is {@code 2m(m + n)}, so each primitive perimeter
 * adds one solution to each of its multiples. That enumerates only actual triangles, a few hundred
 * increments for {@code p <= 1000}, instead of searching over side lengths.
 */
public class Problem39 extends AbstractProblem {

  private static final int LIMIT = 1000;

  @Override
  public Object solve() {
    var solutions = new int[LIMIT + 1];
    for (var m = 2; 2 * m * (m + 1) <= LIMIT; m++) {
      // Opposite parity: n steps by 2 from the parity opposite to m's.
      for (var n = 1 + m % 2; n < m && 2 * m * (m + n) <= LIMIT; n += 2) {
        if (gcd(m, n) == 1) {
          var perimeter = 2 * m * (m + n);
          for (var multiple = perimeter; multiple <= LIMIT; multiple += perimeter) {
            solutions[multiple]++;
          }
        }
      }
    }

    var best = 0;
    // Perimeters are always even.
    for (var p = 2; p <= LIMIT; p += 2) {
      if (solutions[p] > solutions[best]) {
        best = p;
      }
    }
    return best;
  }

  private static int gcd(int a, int b) {
    while (b != 0) {
      var remainder = a % b;
      a = b;
      b = remainder;
    }
    return a;
  }
}
