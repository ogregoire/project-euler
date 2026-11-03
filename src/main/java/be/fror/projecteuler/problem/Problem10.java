/*
 * Copyright 2015 Olivier Grégoire <fror@users.noreply.github.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.fror.projecteuler.problem;

/**
 * <p>
 * The sum of the primes below 10 is 2 + 3 + 5 + 7 = 17.</p>
 * <p>
 * Find the sum of all the primes below two million.</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem10 extends AbstractProblem {

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int limit = 2_000_000;

  private static final int[] SMALL_PRIMES = {2, 3, 5, 7, 11, 13};
  // Slots per period of the marks of 5, 7, 11 and 13: 2 * 5 * 7 * 11 * 13.
  private static final int PERIOD = 10_010;
  // Slots per int block of the sum. Values stay below limit, so a block sums to less than
  // BLOCK * limit, which fits in an int for limit up to 2^31 / BLOCK.
  private static final int BLOCK = 1024;

  @Override
  public Object solve() {
    return sumOfPrimesBelow(limit);
  }

  private static long sumOfPrimesBelow(int limit) {
    // The wheel and the presieve take 2, 3, 5, 7, 11 and 13 out of the general case.
    if (limit <= 17) {
      var sum = 0L;
      for (var p : SMALL_PRIMES) {
        if (p < limit) {
          sum += p;
        }
      }
      return sum;
    }
    var max = limit - 1;
    // Slot x stands for the x-th number coprime to 6 from 5 (5, 7, 11, 13, ...), up to max.
    var size = max / 3;
    if (value(size - 1) > max) {
      size--;
    }
    var composite = new byte[size];
    var top = value(size - 1);
    // The marks of 5, 7, 11 and 13 repeat every 5 * 7 * 11 * 13 * 6 = 30030 values, which is
    // 10010 slots. From p * p, the first period leaves 5, 7, 11 and 13 themselves unmarked, so the
    // pattern only repeats from slot 4: mark up to slot 4 + PERIOD, where it holds one full period.
    var filled = Math.min(size, 4 + PERIOD);
    for (var x = 0; x < 4; x++) {
      var p = value(x);
      var step = 2 * p;
      for (var j = index(p * p); j < filled; j += step) {
        composite[j] = 1;
      }
      for (var j = index(p * (p + 2 + 2 * (x & 1))); j < filled; j += step) {
        composite[j] = 1;
      }
    }
    // Copy everything filled from slot 4 onto what follows, doubling the filled part each time.
    // filled - 4 stays a multiple of PERIOD, so each copy lands in phase.
    while (filled < size) {
      var length = Math.min(filled - 4, size - filled);
      System.arraycopy(composite, 4, composite, filled, length);
      filled += length;
    }
    // Only primes up to sqrt(top) mark anything, since the first multiple marked is p * p.
    // long, because p * p overflows an int for p above 46340.
    for (var x = 4; (long) value(x) * value(x) <= top; x++) {
      if (composite[x] == 0) {
        var p = value(x);
        // The multiples of p in the wheel are p * q for q coprime to 6, so they form two series
        // from p * p and from p * (p + 2) (p = 6k + 5) or p * (p + 4) (p = 6k + 1), each 6p
        // apart, which is 2p slots.
        var step = 2 * p;
        for (var j = index(p * p); j < size; j += step) {
          composite[j] = 1;
        }
        for (var j = index(p * (p + 2 + 2 * (x & 1))); j < size; j += step) {
          composite[j] = 1;
        }
      }
    }
    // long, because the sum overflows an int.
    var sum = 2L + 3;
    // Each block sums in an int, which can't overflow (see BLOCK), and adds to the long once.
    for (var block = 0; block < size; block += BLOCK) {
      var end = Math.min(block + BLOCK, size);
      var blockSum = 0;
      for (var x = block; x < end; x++) {
        // A mask instead of a branch: composite[x] is 0 or 1, so composite[x] - 1 is the int -1
        // (all 32 bits set) for a prime, and 0 for a composite. ANDed with value(x), it keeps the
        // value of a prime and clears the value of a composite to 0.
        blockSum += (composite[x] - 1) & value(x);
      }
      sum += blockSum;
    }
    return sum;
  }

  /** The number that slot {@code x} stands for. */
  private static int value(int x) {
    // Even x give 3x + 5 (6k + 5), odd x give 3x + 4 (6k + 1).
    return 3 * x + 5 - (x & 1);
  }

  /** The slot of {@code v}, which must be coprime to 6 and at least 5. */
  private static int index(int v) {
    // Inverts value(): v / 3 rounds both 3x + 5 and 3x + 4 down to x + 1.
    return v / 3 - 1;
  }

}
