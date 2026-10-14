package be.fror.projecteuler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProblemFactoryTest {

  private final ProblemFactory factory = new ProblemFactory();

  @Test
  public void getClaudeProblemFindsExistingSolution() {
    var problem = factory.getClaudeProblem(23);

    assertTrue(problem.isPresent());
    assertEquals(be.fror.projecteuler.claude.Problem23.class, problem.get().getClass());
    assertEquals(23, problem.get().getId());
  }

  @Test
  public void getClaudeProblemIsEmptyWithoutSolution() {
    assertFalse(factory.getClaudeProblem(1).isPresent());
  }

  @Test
  public void getProblemFindsOriginalSolution() {
    assertEquals(be.fror.projecteuler.problem.Problem23.class, factory.getProblem(23).getClass());
  }

  @Test
  public void getProblemFailsWithoutSolution() {
    assertThrows(ProblemFactory.ProblemNotFoundException.class, () -> factory.getProblem(0));
  }
}
