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
    // 2 is the only even prime, and the sieve only holds odd numbers.
    if (n == 1) {
      return 2;
    }
    // For n >= 6, the n-th prime is below n (ln n + ln ln n); below that, 13 is the 6th prime.
    var max = n <= 6 ? 13 : (int) Math.ceil(n * (Math.log(n) + Math.log(Math.log(n))));
    // Slot i stands for the odd number 2i + 1, up to max included.
    var size = (max - 1) / 2 + 1;
    var composite = new boolean[size];
    // Slot 0 stands for 1, which isn't prime, so the sieve starts at slot 1 (3).
    // Only primes up to sqrt(max) mark anything, since the first multiple marked is p * p, at slot
    // 2i(i + 1); long, because that overflows an int for i above 32767.
    for (var i = 1; 2L * i * (i + 1) < size; i++) {
      if (!composite[i]) {
        var p = 2 * i + 1;
        // Odd multiples of p are 2p apart, which is p slots.
        for (var j = 2 * i * (i + 1); j < size; j += p) {
          composite[j] = true;
        }
      }
    }
    // Slot 0 is never marked, so it counts as the first prime, 2.
    var count = 0;
    for (var i = 0; i < size; i++) {
      // A ternary instead of an if, so the JIT turns it into arithmetic, not a branch on the data.
      count += composite[i] ? 0 : 1;
      if (count == n) {
        return 2 * i + 1;
      }
    }
    throw new IllegalStateException("Sieve too small for n = " + n);
  }

}
