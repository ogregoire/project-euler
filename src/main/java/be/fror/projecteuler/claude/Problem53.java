package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Combinatoric selections.
 *
 * <p>For a given n, C(n, r) grows with r up to n / 2, so the values above the threshold are those
 * with {@code r0 <= r <= n - r0}, where r0 is the smallest r crossing the threshold: there are
 * {@code n - 2 * r0 + 1} of them. And r0 can only grow when n shrinks.
 *
 * <p>So this walks the boundary once, from n = 100 downwards, carrying C(n, r) along with exact
 * integer updates: r goes up while C(n, r) is too small, and each row costs a single step down.
 * That is about 2 * 100 steps, instead of the 5000 cells of Pascal's triangle.
 */
public class Problem53 extends AbstractProblem {

  private static final int MAX_N = 100;
  private static final int THRESHOLD = 1_000_000;

  @Override
  public Object solve() {
    var count = 0;
    var n = MAX_N;
    var r = 0;
    // C(n, r), kept no larger than about THRESHOLD * n so that it fits in a long.
    var c = 1L;
    while (r <= n / 2) {
      if (c > THRESHOLD) {
        count += n - 2 * r + 1;
        // C(n - 1, r) = C(n, r) * (n - r) / n
        c = c * (n - r) / n;
        n--;
      } else {
        // C(n, r + 1) = C(n, r) * (n - r) / (r + 1)
        c = c * (n - r) / (r + 1);
        r++;
      }
    }
    return count;
  }
}
