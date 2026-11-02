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
 * The prime factors of 13195 are 5, 7, 13 and 29.</p>
 * <p>
 * What is the largest prime factor of the number 600851475143 ?</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem3 extends AbstractProblem {

  @Override
  public Object solve() {
    var n = 600851475143L;
    if (n % 2 == 0) {
      n >>= Long.numberOfTrailingZeros(n);
      if (n == 1) {
        return 2L;
      }
    }
    if (n % 3 == 0) {
      do {
        n /= 3;
      } while (n % 3 == 0);
      if (n == 1) {
        return 3L;
      }
    }
    // From 5, alternating +2 and +4 skips every multiple of 2 and 3.
    var divisor = 5L;
    var step = 2L;
    while (divisor * divisor <= n) {
      if (n % divisor == 0) {
        n /= divisor;
      } else {
        divisor += step;
        step = 6 - step;
      }
    }
    return n;
  }

}
