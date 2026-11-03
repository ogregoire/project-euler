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

  private static long tripletProduct(int n) {
    // 3 + 4 + 5 is the smallest sum of a Pythagorean triplet.
    if (n < 12) {
      throw new IllegalArgumentException("n must be at least 12, got " + n);
    }
    // b = (n - c + sqrt(c^2 + 2nc - n^2)) / 2, from a + b = n - c and a^2 + b^2 = c^2. The root is
    // real from c = (sqrt(2) - 1) n; and a + b > c, so c < n / 2.
    for (var c = (int) Math.ceil((Math.sqrt(2) - 1) * n); 2 * c < n; c++) {
      // long, because c^2 + 2nc overflows an int for sums above about 41000.
      var x = (long) c * c + 2L * n * c - (long) n * n;
      var r = (long) Math.sqrt(x);
      // x has the parity of n - c, so a square root r always makes n - c + r even.
      if (r * r == x) {
        var b = (n - c + r) / 2;
        var a = n - c - b;
        // long, because a * b * c overflows an int for sums above about 3900.
        return a * b * c;
      }
    }
    throw new IllegalStateException("No Pythagorean triplet sums to " + n);
  }

}
