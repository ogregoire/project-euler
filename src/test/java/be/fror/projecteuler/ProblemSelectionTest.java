package be.fror.projecteuler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class ProblemSelectionTest {

  private static final SortedSet<Integer> SOLVED = new TreeSet<>(List.of(1, 2, 3, 5, 8, 60));

  @Test
  public void selectsSingleProblem() {
    assertEquals(List.of(3), parse("3"));
  }

  @Test
  public void rangeSkipsUnsolvedProblems() {
    assertEquals(List.of(1, 2, 3, 5), parse("1-5"));
  }

  @Test
  public void allSelectsEverySolvedProblem() {
    assertEquals(List.of(1, 2, 3, 5, 8, 60), parse("all"));
  }

  @Test
  public void keepsOrderGivenWithoutDuplicates() {
    assertEquals(List.of(8, 1, 2, 3, 60), parse("8,1-3", "2", "60"));
  }

  @Test
  public void acceptsSpacesAsSeparators() {
    assertEquals(List.of(1, 3, 5), parse(" 1, 3  5 "));
  }

  @Test
  public void rejectsUnsolvedProblemNamedAlone() {
    assertRejected("Problem 4 not found.", "4");
  }

  @Test
  public void rejectsReversedRange() {
    assertRejected("Empty range: 5-1", "5-1");
  }

  @Test
  public void rejectsAnythingElse() {
    assertRejected("Not a problem, range or 'all': x", "x");
    assertRejected("Not a problem, range or 'all': -5", "-5");
  }

  @Test
  public void rejectsSelectionWithoutSolvedProblems() {
    assertRejected("No problem selected.", "55-59");
    assertRejected("No problem selected.", " ");
  }

  private static List<Integer> parse(String... args) {
    return ProblemSelection.parse(List.of(args), SOLVED);
  }

  private static void assertRejected(String message, String... args) {
    var thrown = assertThrows(IllegalArgumentException.class, () -> parse(args));
    assertEquals(message, thrown.getMessage());
  }
}
