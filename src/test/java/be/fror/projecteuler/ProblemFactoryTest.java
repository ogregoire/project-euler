package be.fror.projecteuler;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

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
    assertEquals("Missing from " + SERVICES_FILE, Set.of(), missing);
    assertEquals("Listed in " + SERVICES_FILE + " but not solutions", Set.of(), stale);
  }

  @Test
  public void getClaudeProblemFindsExistingSolution() {
    var problem = factory.getClaudeProblem(23);

    assertTrue(problem.isPresent());
    assertEquals(be.fror.projecteuler.claude.Problem23.class, problem.get().getClass());
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

  @Test
  public void idsAreThoseOfYourSolutions() {
    var factory = new ProblemFactory(
        List.of(new ThrowingProblem(5, new Error()), new ThrowingProblem(2, new Error())),
        List.of(new ThrowingProblem(9, new Error())));

    assertEquals(List.of(2, 5), List.copyOf(factory.ids()));
  }

  @Test
  public void rejectsTwoSolutionsToTheSameProblem() {
    var first = new ThrowingProblem(7, new Error());
    var second = new ThrowingProblem(7, new Error());

    assertThrows(IllegalArgumentException.class,
        () -> new ProblemFactory(List.of(first, second), List.of()));
  }
}
