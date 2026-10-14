/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;

/**
 *
 * @author Olivier Grégoire
 */
public class Application {

  private final PrintStream output;
  private final Scanner scanner;
  private final ProblemFactory problemFactory;
  private final ProblemRunner problemRunner;

  public Application(PrintStream output, Scanner scanner,
      ProblemFactory problemFactory,
      ProblemRunner problemRunner) {
    this.output = output;
    this.scanner = scanner;
    this.problemFactory = problemFactory;
    this.problemRunner = problemRunner;
  }

  public void start() {
    output.print("Enter problem number: ");
    int problemNumber = scanner.nextInt();

    Problem problem = problemFactory.getProblem(problemNumber);
    Optional<Problem> claudeProblem = problemFactory.getClaudeProblem(problemNumber);

    output.printf("%nRunning problem %d...%n", problemNumber);
    Optional<String> result = problemRunner.run("Yours", problem);
    claudeProblem.ifPresent(p -> {
      Optional<String> claudeResult = problemRunner.run("Claude's", p);
      if (result.isPresent() && claudeResult.isPresent()
          && !result.get().equals(claudeResult.get())) {
        output.printf("\tResults differ!%n");
      }
    });
    output.printf("%n");
  }
}
