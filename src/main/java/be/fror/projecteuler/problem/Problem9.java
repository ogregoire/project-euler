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
 * A Pythagorean triplet is a set of three natural numbers, <var>a</var> &lt; <var>b</var> &lt;
 * <var>c</var>, for which,</p>
 * <div style="text-align:center;"> <var>a</var><sup>2</sup> + <var>b</var><sup>2</sup> =
 * <var>c</var><sup>2</sup></div>
 * <p>
 * For example, 3<sup>2</sup> + 4<sup>2</sup> = 9 + 16 = 25 = 5<sup>2</sup>.</p>
 * <p>
 * There exists exactly one Pythagorean triplet for which <var>a</var> + <var>b</var> + <var>c</var>
 * = 1000.<br />Find the product <var>abc</var>.</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem9 extends AbstractProblem {

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int sum = 1000;

  @Override
  public Object solve() {
    return tripletProduct(sum);
  }

  private static long tripletProduct(int sum) {
    // 3 + 4 + 5 is the smallest sum of a Pythagorean triplet.
    if (sum < 12) {
      throw new IllegalArgumentException("sum must be at least 12, got " + sum);
    }
    // Every triplet is k (m^2 - n^2, 2mn, m^2 + n^2) with m > n >= 1 (Euclid's formula), so its
    // sum is 2k m (m + n): always even.
    if (sum % 2 != 0) {
      throw new IllegalStateException("No Pythagorean triplet sums to " + sum);
    }
    var half = sum / 2;
    // m (m + n) divides half, and n >= 1, so m^2 < half.
    for (var m = 2; m * m < half; m++) {
      for (var n = 1; n < m; n++) {
        var primitiveHalfSum = m * (m + n);
        if (half % primitiveHalfSum == 0) {
          var k = half / primitiveHalfSum;
          // long, because a * b * c overflows an int for sums above about 3900.
          var a = (long) (m * m - n * n) * k;
          var b = 2L * m * n * k;
          var c = (long) (m * m + n * n) * k;
          return a * b * c;
        }
      }
    }
    throw new IllegalStateException("No Pythagorean triplet sums to " + sum);
  }

}
