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
 * By listing the first six prime numbers: 2, 3, 5, 7, 11, and 13, we can see that the 6th prime is
 * 13.</p>
 * <p>
 * What is the 10 001st prime number?</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem7 extends AbstractProblem {

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int n = 10_001;

  @Override
  public Object solve() {
    return nthPrime(n);
  }

  private static int nthPrime(int n) {
    if (n < 1) {
      throw new IllegalArgumentException("n must be at least 1, got " + n);
    }
    // 2 and 3 are the only primes outside the wheel.
    if (n <= 2) {
      return n + 1;
    }
    // For n >= 6, the n-th prime is below n (ln n + ln ln n); below that, 13 is the 6th prime.
    var max = n <= 6 ? 13 : (int) Math.ceil(n * (Math.log(n) + Math.log(Math.log(n))));
    // Slot x stands for the x-th number coprime to 6 from 5 (5, 7, 11, 13, ...); the last slot
    // stands for max or a number just past it.
    var size = max / 3;
    var composite = new boolean[size];
    var top = value(size - 1);
    // Only primes up to sqrt(top) mark anything, since the first multiple marked is p * p.
    // long, because p * p overflows an int for p above 46340.
    for (var x = 0; (long) value(x) * value(x) <= top; x++) {
      if (!composite[x]) {
        var p = value(x);
        // The multiples of p in the wheel are p * q for q coprime to 6, so they form two series
        // from p * p and from p * (p + 2) (p = 6k + 5) or p * (p + 4) (p = 6k + 1), each 6p
        // apart, which is 2p slots.
        var step = 2 * p;
        for (var j = index(p * p); j < size; j += step) {
          composite[j] = true;
        }
        for (var j = index(p * (p + 2 + 2 * (x & 1))); j < size; j += step) {
          composite[j] = true;
        }
      }
    }
    // 2 and 3 come before the wheel.
    var count = 2;
    for (var x = 0; x < size; x++) {
      // A ternary instead of an if, so the JIT turns it into arithmetic, not a branch on the data.
      count += composite[x] ? 0 : 1;
      if (count == n) {
        return value(x);
      }
    }
    throw new IllegalStateException("Sieve too small for n = " + n);
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
