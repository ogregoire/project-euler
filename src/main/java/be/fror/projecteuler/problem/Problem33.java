/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import be.fror.projecteuler.math.IntMath2;

/**
 *
 * @author Olivier Grégoire
 */
public class Problem33 extends AbstractProblem {

  @Override
  public Object solve() {
    int n = 1;
    int d = 1;
    for (int i = 1; i < 10; i++) {
      for (int j = i + 1; j < 10; j++) {
        float x = 9F * i * j / (10F * i - j);
        if (((int) x) == x && x < 10F) {
          n *= i;
          d *= j;
        }
      }
    }
    return d / IntMath2.gcd(d, n);
  }

}
