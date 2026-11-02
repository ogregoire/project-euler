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
 * The four adjacent digits in the 1000-digit number that have the greatest product are 9 &times; 9
 * &times; 8 &times; 9 = 5832.</p>
 * <code><pre>
 * 73167176531330624919225119674426574742355349194934
 * 96983520312774506326239578318016984801869478851843
 * 85861560789112949495459501737958331952853208805511
 * 12540698747158523863050715693290963295227443043557
 * 66896648950445244523161731856403098711121722383113
 * 62229893423380308135336276614282806444486645238749
 * 30358907296290491560440772390713810515859307960866
 * 70172427121883998797908792274921901699720888093776
 * 65727333001053367881220235421809751254540594752243
 * 52584907711670556013604839586446706324415722155397
 * 53697817977846174064955149290862569321978468622482
 * 83972241375657056057490261407972968652414535100474
 * 82166370484403199890008895243450658541227588666881
 * 16427171479924442928230863465674813919123162824586
 * 17866458359124566529476545682848912883142607690042
 * 24219022671055626321111109370544217506941658960408
 * 07198403850962455444362981230987879927244284909188
 * 84580156166097919133875499200524063689912560717606
 * 05886116467109405077541002256983155200055935729725
 * 71636269561882670428252483600823257530420752963450
 * </pre></code>
 * <p>
 * Find the thirteen adjacent digits in the 1000-digit number that have the greatest product. What
 * is the value of this product?</p>
 *
 * @author Olivier Grégoire &lt;fror@users.noreply.github.com&gt;
 */
public class Problem8 extends AbstractProblem {

  // Volatile, so the JIT reloads them on every call and can't fold or hoist the computation.
  // Each line ends with \ so the text block holds the 1000 digits without line breaks.
  private volatile String number = """
      73167176531330624919225119674426574742355349194934\
      96983520312774506326239578318016984801869478851843\
      85861560789112949495459501737958331952853208805511\
      12540698747158523863050715693290963295227443043557\
      66896648950445244523161731856403098711121722383113\
      62229893423380308135336276614282806444486645238749\
      30358907296290491560440772390713810515859307960866\
      70172427121883998797908792274921901699720888093776\
      65727333001053367881220235421809751254540594752243\
      52584907711670556013604839586446706324415722155397\
      53697817977846174064955149290862569321978468622482\
      83972241375657056057490261407972968652414535100474\
      82166370484403199890008895243450658541227588666881\
      16427171479924442928230863465674813919123162824586\
      17866458359124566529476545682848912883142607690042\
      24219022671055626321111109370544217506941658960408\
      07198403850962455444362981230987879927244284909188\
      84580156166097919133875499200524063689912560717606\
      05886116467109405077541002256983155200055935729725\
      71636269561882670428252483600823257530420752963450""";
  private volatile int window = 13;

  @Override
  public Object solve() {
    return greatestProduct(number, window);
  }

  private static long greatestProduct(String number, int window) {
    if (window < 1 || window > number.length()) {
      throw new IllegalArgumentException(
          "window must be between 1 and " + number.length() + ", got " + window);
    }
    var max = 0L;
    // The start behaves like a zero just before the first digit.
    var zero = -1;
    // Each pass starts right after a zero and ends at the next one, or at the end of the number.
    while (zero + window < number.length()) {
      // The first full window after the zero ends at zero + window. Reading it backwards finds
      // the last zero in it first, and skips every digit before that zero.
      var end = zero + window;
      // long, because 9^13 overflows an int.
      var product = 1L;
      // The number of digits since the last zero.
      var count = 0;
      for (var i = end; i > zero; i--) {
        var digit = number.charAt(i) - '0';
        if (digit == 0) {
          break;
        }
        product *= digit;
        count++;
      }
      if (count == window && product > max) {
        max = product;
      }
      // Resume rightwards from the end of that window.
      zero = number.length();
      for (var i = end + 1; i < number.length(); i++) {
        var digit = number.charAt(i) - '0';
        if (digit == 0) {
          zero = i;
          break;
        }
        count++;
        product *= digit;
        // The digit leaving the window is nonzero, since there's no zero in the last count digits.
        if (count > window) {
          product /= number.charAt(i - window) - '0';
        }
        if (count >= window && product > max) {
          max = product;
        }
      }
    }
    return max;
  }

}
