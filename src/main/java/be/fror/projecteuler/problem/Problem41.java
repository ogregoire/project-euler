/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import be.fror.projecteuler.math.Primes;

import java.util.Arrays;

/**
 * We shall say that an n-digit number is pandigital if it makes use of all the digits 1 to n
 * exactly once. For example, 2143 is a 4-digit pandigital and is also prime.
 * <p>
 * What is the largest n-digit pandigital prime that exists?
 *
 */
public class Problem41 extends AbstractProblem {

  @Override
  public Object solve() {
    int[] digits = {7, 6, 5, 4, 3, 2, 1}; // No prime 8- or 9-pandigital number exists: they're all divisible by 3.
    int max = Integer.MIN_VALUE;
    for (int count = 0; count <= digits.length; count++) {
      int[] perm = Arrays.copyOfRange(digits, count, digits.length);
      do { // decreasing lexicographic order.
        int n = 0;
        for (int i : perm) {
          n = n * 10 + i;
        }
        if (n > max && Primes.isPrime(n)) {
          max = n;
        }
      } while (previousPermutation(perm));
      if (max != Integer.MIN_VALUE) {
        return max;
      }
    }
    return null;
  }

  /**
   * Rearranges {@code a} into the permutation just before it in lexicographic order.
   *
   * @return {@code false} if {@code a} was already the first permutation, in which case it is
   *     left unchanged
   */
  private static boolean previousPermutation(int[] a) {
    int i = a.length - 2;
    while (i >= 0 && a[i] <= a[i + 1]) {
      i--;
    }
    if (i < 0) {
      return false;
    }
    int j = a.length - 1;
    while (a[j] >= a[i]) {
      j--;
    }
    int tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
    for (int lo = i + 1, hi = a.length - 1; lo < hi; lo++, hi--) {
      tmp = a[lo];
      a[lo] = a[hi];
      a[hi] = tmp;
    }
    return true;
  }
}
