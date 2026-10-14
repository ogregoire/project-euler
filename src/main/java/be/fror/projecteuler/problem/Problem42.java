/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import be.fror.projecteuler.util.Resources;

import java.util.List;

/**
 *
 * @author Olivier Grégoire
 */
public class Problem42 extends AbstractProblem {

  private static final List<String> words = Resources.readWords("words.txt");

  @Override
  public Object solve() {
    boolean[] isTriangle = new boolean[1000];
    for (int i = 1, n = i; n < isTriangle.length; i++, n += i) {
      isTriangle[n] = true;
    }
    int count = 0;
    for (String word : words) {
      int len = word.length();
      int sum = -len * ('A' - 1);
      for (int i = 0; i < len; i++) {
        sum += word.charAt(i);
      }
      if (isTriangle[sum]) {
        count++;
      }
    }
    return count;
  }
}
