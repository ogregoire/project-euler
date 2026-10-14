package be.fror.projecteuler.claude;

import be.fror.projecteuler.problem.AbstractProblem;

/**
 * Pandigital prime.
 *
 * <p>The digit sum of an n-digit pandigital number is {@code n(n + 1) / 2}. For n = 2, 3, 5, 6, 8
 * and 9 that sum is a multiple of 3, so every such number is too, and can't be prime. The other
 * lengths are tried from the longest, and their permutations from the largest down, so the first
 * prime met is the answer and only a few dozen numbers are ever tested.
 */
public class Problem41 extends AbstractProblem {

  @Override
  public Object solve() {
    for (var length = 9; length >= 1; length--) {
      if (length * (length + 1) / 2 % 3 == 0) {
        continue;
      }
      var digits = new int[length];
      for (var i = 0; i < length; i++) {
        digits[i] = length - i;
      }
      do {
        var n = 0;
        for (var digit : digits) {
          n = n * 10 + digit;
        }
        if (isPrime(n)) {
          return n;
        }
      } while (previousPermutation(digits));
    }
    throw new IllegalStateException("No pandigital prime exists");
  }

  /**
   * Rearranges {@code digits} into the previous permutation in lexicographic order, and returns
   * whether there was one.
   */
  private static boolean previousPermutation(int[] digits) {
    var i = digits.length - 2;
    while (i >= 0 && digits[i] < digits[i + 1]) {
      i--;
    }
    if (i < 0) {
      return false;
    }
    var j = digits.length - 1;
    while (digits[j] > digits[i]) {
      j--;
    }
    swap(digits, i, j);
    for (int left = i + 1, right = digits.length - 1; left < right; left++, right--) {
      swap(digits, left, right);
    }
    return true;
  }

  private static void swap(int[] array, int i, int j) {
    var tmp = array[i];
    array[i] = array[j];
    array[j] = tmp;
  }

  private static boolean isPrime(int n) {
    if (n < 2) {
      return false;
    }
    if (n % 2 == 0) {
      return n == 2;
    }
    for (var d = 3; d * d <= n; d += 2) {
      if (n % d == 0) {
        return false;
      }
    }
    return true;
  }
}
