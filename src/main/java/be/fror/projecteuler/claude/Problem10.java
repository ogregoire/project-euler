package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Summation of primes.
 *
 * <p>A sieve of Eratosthenes that only represents odd numbers, since 2 is the only even prime,
 * with two tricks for speed:
 *
 * <ul>
 *   <li>The smallest primes cost the most to cross off, as they have the most multiples. But their
 *       multiples form a pattern that repeats every 3 * 5 * 7 * 11 * 13 odd numbers, so that
 *       pattern is crossed off once and then copied over the whole sieve in bulk.
 *   <li>The summation is branch-free: a branch per number would be mispredicted all the time, as
 *       primes are irregular. It also adds up blocks in an {@code int}, which the JIT can
 *       vectorize, and only adds each block's total to the {@code long}.
 * </ul>
 */
public class Problem10 extends AbstractProblem {

  private static final int LIMIT = 2_000_000;

  private static final int[] PRESIEVED_PRIMES = {3, 5, 7, 11, 13};
  private static final int PATTERN_LENGTH = 3 * 5 * 7 * 11 * 13;

  /** Small enough that a block's sum fits in an {@code int}. */
  private static final int BLOCK_SIZE = 256;

  @Override
  public Object solve() {
    // Index i stands for the odd number 2i + 1. Composite ones are 1, primes 0.
    var size = LIMIT / 2;
    var composite = new byte[size];
    for (var p : PRESIEVED_PRIMES) {
      for (var i = p / 2; i < PATTERN_LENGTH; i += p) {
        composite[i] = 1;
      }
    }
    for (var length = PATTERN_LENGTH; length < size; length *= 2) {
      System.arraycopy(composite, 0, composite, length, Math.min(length, size - length));
    }
    // The pattern crossed off the presieved primes too, and missed 1.
    for (var p : PRESIEVED_PRIMES) {
      composite[p / 2] = 0;
    }
    composite[0] = 1;

    for (var i = PRESIEVED_PRIMES[PRESIEVED_PRIMES.length - 1] / 2 + 1; ; i++) {
      var p = 2 * i + 1;
      var start = p * p / 2;
      if (start >= size) {
        break;
      }
      if (composite[i] == 0) {
        // Consecutive odd multiples of p are 2p apart, which is p apart in odd-only indexes.
        for (var j = start; j < size; j += p) {
          composite[j] = 1;
        }
      }
    }

    var sum = 2L;
    for (var block = 0; block < size; block += BLOCK_SIZE) {
      var blockSum = 0;
      for (var i = block; i < Math.min(block + BLOCK_SIZE, size); i++) {
        // composite[i] - 1 is all ones for a prime, and zero otherwise.
        blockSum += (2 * i + 1) & (composite[i] - 1);
      }
      sum += blockSum;
    }
    return sum;
  }
}
