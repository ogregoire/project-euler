package be.fror.projecteuler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

public class ProblemSelectionTest {

  private static final SortedSet<Integer> SOLVED = new TreeSet<>(List.of(1, 2, 3, 5, 8, 60));

  @Test
  public void selectsSingleProblem() {
    assertThat(parse("3")).isEqualTo(List.of(3));
  }

  @Test
  public void rangeSkipsUnsolvedProblems() {
    assertThat(parse("1-5")).isEqualTo(List.of(1, 2, 3, 5));
  }

  @Test
  public void allSelectsEverySolvedProblem() {
    assertThat(parse("all")).isEqualTo(List.of(1, 2, 3, 5, 8, 60));
  }

  @Test
  public void keepsOrderGivenWithoutDuplicates() {
    assertThat(parse("8,1-3", "2", "60")).isEqualTo(List.of(8, 1, 2, 3, 60));
  }

  @Test
  public void acceptsSpacesAsSeparators() {
    assertThat(parse(" 1, 3  5 ")).isEqualTo(List.of(1, 3, 5));
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
    assertThatThrownBy(() -> parse(args))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(message);
  }
}
