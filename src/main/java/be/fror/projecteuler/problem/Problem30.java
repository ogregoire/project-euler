/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

/**
 *
 * @author Olivier Grégoire
 */
public class Problem30 extends AbstractProblem {

  @Override
  public Object solve() {
    int N = 5;
    int MAX_DIGITS = 100;

    long[] powers = new long[10];
    long[] diffs = new long[9];
    int[] counts = new int[10];
    int[] digits = new int[MAX_DIGITS];
    digits[0] = 1;
    long totalSum = 0L;
    long sumPow = 1L;
    int pos = 0;
    long number = 1;

    for (int i = 1; i < 10; i++) {
      long power = 1;
      for (int e = 0; e < N; e++) {
        power *= i;
      }
      powers[i] = power;
      diffs[i - 1] = powers[i] - powers[i - 1];
    }

    for (;;) {
      for (int i = 0; i < 10; i++) {
        counts[i] = 0;
      }
      for (int i = 0; i <= pos; i++) {
        counts[digits[i]]++;
      }
      for (long i = sumPow; i != 0L; i /= 10L) {
        counts[(int) (i % 10)]--;
      }
      for (int i = 1; i < 10; i++) {
        if (counts[i] != 0) {
          break;
        }
        if (i == 9 && pos > 0) {
          // BINGO, sumPow is a number we're looking for.
          totalSum += sumPow;
        }
      }

      if (number * 10 + 9 <= sumPow + powers[9]) {
        int i = digits[pos++];
        digits[pos] = i;
        number = number * 10 + i;
        sumPow += powers[i];
      } else {
        while (pos > 0 && digits[pos] == 9) {
          pos--;
          number /= 10;
          sumPow -= powers[9];
        }
        if (digits[pos] == 9) {
          break;
        }
        number++;
        sumPow += diffs[digits[pos]++];
      }

    }

    return totalSum;
  }

}
