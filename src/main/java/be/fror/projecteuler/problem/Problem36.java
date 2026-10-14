/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

/**
 *
 * @author Olivier
 */
public class Problem36 extends AbstractProblem {

  @Override
  public Object solve() {
    int N = 1000000;

    int[] powersOf10 = new int[(int) Math.log10(N)];
    for (int i = 0, p = 1; i < powersOf10.length; i++, p *= 10) {
      powersOf10[i] = p;
    }

    int sum = 0;

    int powIndex = 0, nextPowIndex = 1;
    outer:
    for (int n = 1, limit = powersOf10[powersOf10.length / 2]; n < limit; n++) {
      if (n == powersOf10[nextPowIndex]) {
        powIndex = nextPowIndex++;
      }

            // n1: the pivot *is* the last digit
      // ex: 12 -> 121
      int n1 = n * powersOf10[powIndex];
            // n2: the pivot *is after* the last digit
      // ex: 12 -> 1221
      int n2 = n * powersOf10[nextPowIndex];

      if (powIndex > 0) {
        for (int tmp = n, p = powIndex; p > 0; p--) {
          int i = tmp / powersOf10[p];
          int d = i * powersOf10[powIndex - p];
          n1 += d;
          if ((n1 & 1) == 0) {
            continue outer; // even numbers cannot be binary palindromes
          }
          n2 += d;
          tmp -= i * powersOf10[p];
        }
        n2 += (n % 10) * powersOf10[powIndex]; // duplicate the last digit
      } else if ((n & 1) == 0) {
        continue; // even numbers cannot be binary palindromes
      } else {
        n2 += n; // duplicate the last digit
      }

      // n1 and n2 are now both palindromic in base 10.
      boolean isBinaryPalindromic;
      isBinaryPalindromic = true;
      for (int low = 2, high = Integer.highestOneBit(n1) >> 1; low < high; low <<= 1, high >>= 1) {
        if (((n1 & low) == 0) != ((n1 & high) == 0)) {
          isBinaryPalindromic = false;
          break;
        }
      }
      if (isBinaryPalindromic) {
        sum += n1;
      }

      isBinaryPalindromic = true;
      for (int low = 2, high = Integer.highestOneBit(n2) >> 1; low < high; low <<= 1, high >>= 1) {
        if (((n2 & low) == 0) != ((n2 & high) == 0)) {
          isBinaryPalindromic = false;
          break;
        }
      }
      if (isBinaryPalindromic) {
        sum += n2;
      }
    }
    return sum;
  }
}
