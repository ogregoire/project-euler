/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

/**
 *
 * @author Olivier Grégoire
 */
public interface Problem {

  /**
   * Gets the problem number for this problem.
   *
   * @return the problem number.
   */
  int getId();

  /**
   * Solves the problem, returning an answer to it.
   *
   * @return an answer to the problem.
   */
  Object solve();
}
