package be.fror.projecteuler;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

public class ProblemFactoryTest {

  private static final String SERVICES_FILE = "META-INF/services/be.fror.projecteuler.Problem";

  private final ProblemFactory factory = ProblemFactory.load();

  @Test
  public void everySolutionClassIsRegistered() throws Exception {
    var classes = Path.of(Problem.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Set<String> compiled;
    try (var files = Files.walk(classes)) {
      compiled = files
          .map(file -> classes.relativize(file).toString().replace(File.separatorChar, '/'))
          .filter(name -> name.matches("be/fror/projecteuler/(problem|claude)/Problem\\d+\\.class"))
          .map(name -> name.replace('/', '.').replace(".class", ""))
          .collect(toSet());
    }
    var registered = ServiceLoader.load(Problem.class).stream()
        .map(provider -> provider.type().getName())
        .collect(toSet());

    var missing = new TreeSet<>(compiled);
    missing.removeAll(registered);
    var stale = new TreeSet<>(registered);
    stale.removeAll(compiled);
    assertThat(missing).as("Missing from " + SERVICES_FILE).isEmpty();
    assertThat(stale).as("Listed in " + SERVICES_FILE + " but not solutions").isEmpty();
  }

  @Test
  public void getClaudeProblemFindsExistingSolution() {
    var problem = factory.getClaudeProblem(23);

    assertThat(problem).containsInstanceOf(be.fror.projecteuler.claude.Problem23.class);
  }

  @Test
  public void getClaudeProblemIsEmptyWithoutSolution() {
    var factory = new ProblemFactory(List.of(new ThrowingProblem(1, new Error())), List.of());

    assertThat(factory.getClaudeProblem(1)).isEmpty();
  }

  @Test
  public void getProblemFindsOriginalSolution() {
    assertThat(factory.getProblem(23))
        .isExactlyInstanceOf(be.fror.projecteuler.problem.Problem23.class);
  }

  @Test
  public void getProblemFailsWithoutSolution() {
    assertThatThrownBy(() -> factory.getProblem(0))
        .isInstanceOf(ProblemFactory.ProblemNotFoundException.class);
  }

  @Test
  public void idsAreThoseOfYourSolutions() {
    var factory = new ProblemFactory(
        List.of(new ThrowingProblem(5, new Error()), new ThrowingProblem(2, new Error())),
        List.of(new ThrowingProblem(9, new Error())));

    assertThat(factory.ids()).containsExactly(2, 5);
  }

  @Test
  public void rejectsTwoSolutionsToTheSameProblem() {
    var first = new ThrowingProblem(7, new Error());
    var second = new ThrowingProblem(7, new Error());

    assertThatThrownBy(() -> new ProblemFactory(List.of(first, second), List.of()))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
