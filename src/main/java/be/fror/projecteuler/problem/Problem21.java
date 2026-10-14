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
public class Problem21 extends AbstractProblem {

  @Override
  public Object solve() {
    var N = 10000;
    var sumOfDivisors = new int[N];
    var sumOfAmicableNumbers = 0;
    for (var number = 1; number < N; number++) {
      for (var multiple = number + number; multiple < N; multiple += number) {
        sumOfDivisors[multiple] += number;
      }
      if (sumOfDivisors[number] < number && number == sumOfDivisors[sumOfDivisors[number]]) {
        sumOfAmicableNumbers += number + sumOfDivisors[number];
      }
    }
    return sumOfAmicableNumbers;
  }
}
