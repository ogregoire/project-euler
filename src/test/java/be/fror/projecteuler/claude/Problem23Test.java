package be.fror.projecteuler.claude;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Problem23Test {

  @Test
  public void solveGivesSameAnswerAsOriginalSolution() {
    assertEquals(new be.fror.projecteuler.problem.Problem23().solve(), new Problem23().solve());
  }
}
