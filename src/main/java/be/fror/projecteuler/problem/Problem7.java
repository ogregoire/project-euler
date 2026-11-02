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

  private static final int[] FIRST_PRIMES = {2, 3, 5, 7, 11, 13};

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int n = 10_001;

  @Override
  public Object solve() {
    return nthPrime(n);
  }

  private static int nthPrime(int n) {
    if (n <= 6) {
      if (n < 1) {
        throw new IllegalArgumentException("n must be at least 1, got " + n);
      }
      return FIRST_PRIMES[n - 1];
    }
    // For n >= 6, the n-th prime is below n (ln n + ln ln n).
    var max = (int) Math.ceil(n * (Math.log(n) + Math.log(Math.log(n))));
    // The sieve only holds the numbers 6k + 1 and 6k + 5 from 7; 2, 3 and 5 are outside it.
    var composite = new boolean[max / 3];
    // 5 is outside the sieve, but its multiples 5 * q, q being 5 or a sieve value, are inside.
    if (25 <= max) {
      composite[index(25)] = true;
    }
    for (var y = 0; 5 * value(y) <= max; y++) {
      composite[index(5 * value(y))] = true;
    }
    var count = 3;
    for (var x = 0; x < composite.length; x++) {
      if (composite[x]) {
        continue;
      }
      var p = value(x);
      count++;
      if (count == n) {
        return p;
      }
      // Only multiples p * q with q in the sieve too can be in the sieve; q starts at p.
      for (var y = x; ; y++) {
        // long, because p * q overflows an int for p above 46340.
        var multiple = (long) p * value(y);
        if (multiple > max) {
          break;
        }
        composite[index((int) multiple)] = true;
      }
    }
    throw new IllegalStateException("Sieve too small for n = " + n);
  }

  /** The number that sieve index {@code x} stands for. */
  private static int value(int x) {
    if (x % 2 == 0) {
      return 6 * (x / 2) + 7;
    }
    return 6 * (x / 2) + 11;
  }

  /** The sieve index of {@code v}, which must be 6k + 1 or 6k + 5 and at least 7. */
  private static int index(int v) {
    if (v % 6 == 1) {
      return (v - 7) / 6 * 2;
    }
    return (v - 11) / 6 * 2 + 1;
  }

}
