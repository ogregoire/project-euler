/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import java.util.Arrays;

/**
 *
 * @author Olivier Grégoire
 */
public class Problem12 extends AbstractProblem {

  // Volatile, so the JIT reloads it on every call and can't fold or hoist the computation.
  private volatile int divisors = 500;

  @Override
  public Object solve() {
    return firstTriangleNumberWithOverDivisors(divisors);
  }

  private static int firstTriangleNumberWithOverDivisors(int divisors) {
    // The largest n whose T(n) = n (n + 1) / 2 fits in an int, from the inverse of T.
    var maxN = (int) ((Math.sqrt(8.0 * Integer.MAX_VALUE + 1) - 1) / 2);
    // count[x] is the number of divisors of x. No number below 2^31 has more than 1600 divisors,
    // so a short is enough.
    var size = 256;
    var count = new short[size];
    sieve(count, 1, size);
    // n and n + 1 share no factor, and the divisor count multiplies over coprime factors, so
    // d(T(n)) is the product of the counts of n and n + 1, with the 2 taken from the even one.
    // Odd n, two at a time: T(n) = n * ((n + 1) / 2) and T(n + 1) = ((n + 1) / 2) * (n + 2)
    // share the count of (n + 1) / 2.
    var n = 1;
    while (true) {
      for (; n + 2 < size && n + 1 <= maxN; n += 2) {
        var half = count[(n + 1) / 2];
        if (count[n] * half > divisors) {
          return n * ((n + 1) / 2);
        }
        if (half * count[n + 2] > divisors) {
          return (n + 1) / 2 * (n + 2);
        }
      }
      if (n + 1 > maxN) {
        // maxN is odd, so T(maxN) is left over on its own.
        if (n == maxN && count[n] * count[(n + 1) / 2] > divisors) {
          return n * ((n + 1) / 2);
        }
        throw new IllegalStateException(
            "No triangle number that fits in an int has over " + divisors + " divisors");
      }
      var from = size;
      size *= 2;
      count = Arrays.copyOf(count, size);
      sieve(count, from, size);
    }
  }

  /** Adds 1 to {@code count[x]} for every divisor of every x in [{@code from}, {@code to}). */
  private static void sieve(short[] count, int from, int to) {
    for (var i = 1; i < to; i++) {
      // The first multiple of i at or after from.
      var start = from <= i ? i : (from + i - 1) / i * i;
      for (var j = start; j < to; j += i) {
        count[j]++;
      }
    }
  }

}
