package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Longest Collatz sequence.
 *
 * <p>Chain lengths are memoized: a sequence starting at {@code n} is only followed until it drops
 * below {@code n}, where the length is already known. Following it is made cheap three ways:
 *
 * <ul>
 *   <li>An even {@code n} drops below itself in one step, and {@code n = 4k + 1} in three steps
 *       (to {@code 3k + 1}), so neither needs a loop. Only {@code n = 4k + 3} is followed.
 *   <li>The sequence is followed in jumps of {@code JUMP_BITS} steps, where an odd {@code x} goes
 *       straight to {@code (3x + 1) / 2}. The parities met along the next {@code JUMP_BITS} steps
 *       depend only on the low {@code JUMP_BITS} bits, so writing
 *       {@code x = 2^JUMP_BITS * high + low}, those steps lead to {@code 3^c * high + offset},
 *       where {@code c} (the number of odd values met) and {@code offset} are tabulated per
 *       {@code low}.
 *   <li>Small numbers, whose jumps could run through the 1, 2, 1 cycle, are followed one step at a
 *       time, without branching on parity, as the branch would be unpredictable.
 * </ul>
 */
public class Problem14 extends AbstractProblem {

  private static final int LIMIT = 1_000_000;

  private static final int JUMP_BITS = 8;

  @Override
  public Object solve() {
    var jumpSize = 1 << JUMP_BITS;
    var multipliers = new long[jumpSize];
    var offsets = new long[jumpSize];
    var jumpSteps = new int[jumpSize];
    for (var low = 0; low < jumpSize; low++) {
      var value = (long) low;
      var multiplier = 1L;
      var steps = JUMP_BITS;
      for (var i = 0; i < JUMP_BITS; i++) {
        if ((value & 1) == 0) {
          value >>>= 1;
        } else {
          value = (3 * value + 1) >>> 1;
          multiplier *= 3;
          // The chain counts 3x + 1 and the halving after it as two terms.
          steps++;
        }
      }
      multipliers[low] = multiplier;
      offsets[low] = value;
      jumpSteps[low] = steps;
    }

    var lengths = new int[LIMIT];
    lengths[1] = 1;
    var best = 1;
    for (var n = 2; n < LIMIT; n++) {
      int length;
      if ((n & 1) == 0) {
        length = lengths[n >>> 1] + 1;
      } else if ((n & 3) == 1) {
        length = lengths[(3 * n + 1) >>> 2] + 3;
      } else if (n < jumpSize) {
        var steps = 0;
        var t = (long) n;
        while (t >= n) {
          var odd = t & 1;
          // When odd: (t + 2t + 1) / 2 = (3t + 1) / 2.
          t = (t + odd * (2 * t + 1)) >>> 1;
          steps += 1 + (int) odd;
        }
        length = lengths[(int) t] + steps;
      } else {
        var steps = 0;
        var t = (long) n;
        do {
          var low = (int) t & (jumpSize - 1);
          t = (t >>> JUMP_BITS) * multipliers[low] + offsets[low];
          steps += jumpSteps[low];
        } while (t >= n);
        length = lengths[(int) t] + steps;
      }
      lengths[n] = length;
      if (length > lengths[best]) {
        best = n;
      }
    }
    return best;
  }
}
