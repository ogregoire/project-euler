/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler.problem;

import be.fror.projecteuler.Problem;

/**
 *
 * @author Olivier Grégoire
 */
public abstract class AbstractProblem implements Problem {

  @Override
  public final int getId() {
    return Integer.valueOf(getClass().getSimpleName().substring(7));
  }

}
