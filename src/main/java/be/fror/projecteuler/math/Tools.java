/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.math;

import java.math.BigInteger;

/**
 *
 * @author Olivier Grégoire
 */
public class Tools {

  public static boolean isPalindromic(int n) {
    int len = 1 + (int) Math.log10(n);
    int[] x = new int[len / 2];
    for (int i = 0; i < x.length; i++) {
      int q = n / 10;
      x[i] = n - q * 10;
      n = q;
    }
    if ((len & 1) != 0) {
      n /= 10;
    }
    for (int i = x.length - 1; i >= 0; i--) {
      int q = n / 10;
      if (n - q * 10 != x[i]) {
        return false;
      }
      n = q;
    }
    return true;
  }

  /**
   * Returns {@code n!}.
   *
   * @param n must be non-negative
   * @return the factorial of {@code n}
   */
  public static BigInteger factorial(int n) {
    BigInteger result = BigInteger.ONE;
    for (int i = 2; i <= n; i++) {
      result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
  }

}
