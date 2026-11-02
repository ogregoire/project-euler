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
 * A palindromic number reads the same both ways. The largest palindrome made from the product of
 * two 2-digit numbers is 9009 = 91 &times; 99.</p>
 * <p>
 * Find the largest palindrome made from the product of two 3-digit numbers.</p>
 *
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem4 extends AbstractProblem {

  @Override
  public Object solve() {
    for (var i = 9; i >= 1; i--) {
      // An odd p, ending in i, has only odd factors.
      var step = ((i & 1) + 1) * 11;
      var start = 1001 - step;
      for (var j = 9; j >= 0; j--) {
        for (var k = 9; k >= 0; k--) {
          var p = i * 100001 + j * 10010 + k * 1100;
          for (var a = start; a >= 100; a -= step) {
            var b = p / a;
            if (b >= 1000) {
              break;
            }
            if (a * b == p && b >= 100) {
              return p;
            }
          }
        }
      }
    }
    throw new IllegalStateException("No palindrome found");
  }

}
