package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * 10001st prime.
 *
 * <p>A sieve of Eratosthenes over odd numbers only, sized just above the n-th prime with the bound
 * {@code p(n) < n (ln n + ln ln n)} (valid for n &ge; 6). Skipping even numbers halves the memory
 * and the work, and the tight bound avoids sieving far beyond the answer. Crossing out and counting
 * are separate passes, so the counting pass is a branch-free loop.
 */
public class Problem7 extends AbstractProblem {

  private static final int RANK = 10_001;

  @Override
  public Object solve() {
    var logRank = Math.log(RANK);
    var limit = (int) (RANK * (logRank + Math.log(logRank)));

    // Index i stands for the odd number 2i + 1.
    var size = (limit - 1) / 2 + 1;
    var composite = new boolean[size];
    for (var i = 1; (2 * i + 1) * (2 * i + 1) <= limit; i++) {
      if (!composite[i]) {
        // Odd multiples of p = 2i + 1 from p * p: p * p has index 2i(i + 1), and stepping by 2p
        // is a step of p indices.
        var p = 2 * i + 1;
        for (var j = 2 * i * (i + 1); j < size; j += p) {
          composite[j] = true;
        }
      }
    }

    var count = 1; // 2 is prime but has no index.
    for (var i = 1; i < size; i++) {
      count += composite[i] ? 0 : 1;
      if (count == RANK) {
        return 2 * i + 1;
      }
    }
    throw new IllegalStateException("Sieve bound too small");
  }
}
