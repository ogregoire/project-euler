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
 * The sum of the squares of the first ten natural numbers is,</p>
 * <div style="text-align:center">1<sup>2</sup> + 2<sup>2</sup> + ... + 10<sup>2</sup> = 385</div>
 * <p>
 * The square of the sum of the first ten natural numbers is,</p>
 * <div style="text-align:center">(1 + 2 + ... + 10)<sup>2</sup> = 55<sup>2</sup> = 3025</div>
 * <p>
 * Hence the difference between the sum of the squares of the first ten natural numbers and the
 * square of the sum is 3025 &minus; 385 = 2640.</p>
 * <p>
 * Find the difference between the sum of the squares of the first one hundred natural numbers and
 * the square of the sum.</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem6 extends AbstractProblem {

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int n = 100;

  @Override
  public Object solve() {
    return sumSquareDifference(n);
  }

  private static int sumSquareDifference(int to) {
    int squareOfSum = to * (to + 1) / 2;
    squareOfSum *= squareOfSum;

    int sumOfSquares = to * (to + 1) * (2 * to + 1) / 6;

    return squareOfSum - sumOfSquares;
  }
}
