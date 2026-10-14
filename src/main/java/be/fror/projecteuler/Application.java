/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fror.projecteuler;

import be.fror.projecteuler.ProblemRunner.Outcome;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Olivier Grégoire
 */
public class Application {

  /** Exit code when a solution failed, timed out, or disagrees with the other solution. */
  public static final int EXIT_FAILURE = 1;

  /** Exit code when the selection of problems is invalid. */
  public static final int EXIT_USAGE = 2;

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

  /**
   * Runs the problems selected by the arguments, or asks which ones if there are none.
   *
   * @param args the selection, such as {@code 23}, {@code 1-10}, {@code 1,5,20-30} or {@code all}.
   * @return the process exit code: 0 if every solution succeeded and agrees, else
   *     {@link #EXIT_FAILURE} or {@link #EXIT_USAGE}.
   */
  public int start(String[] args) {
    var selection = args.length > 0 ? List.of(args) : List.of(prompt());
    List<Integer> ids;
    try {
      ids = ProblemSelection.parse(selection, problemFactory.ids());
    } catch (IllegalArgumentException e) {
      output.printf("%s%n%s%n", e.getMessage(), ProblemSelection.USAGE);
      return EXIT_USAGE;
    }

    var rows = ids.stream().map(this::run).toList();
    output.printf("%n");
    if (rows.size() > 1) {
      printSummary(rows);
    }
    return rows.stream().allMatch(Row::succeeded) ? 0 : EXIT_FAILURE;
  }

  private String prompt() {
    output.print("Enter problems (e.g. 23, 1-10 or all): ");
    return scanner.nextLine();
  }

  private Row run(int id) {
    output.printf("%nRunning problem %d...%n", id);
    var yours = problemRunner.run("Yours", problemFactory.getProblem(id));
    var claude = problemFactory.getClaudeProblem(id)
        .map(problem -> problemRunner.run("Claude's", problem))
        .orElse(null);
    var row = new Row(id, yours, claude);
    if (row.differ()) {
      output.printf("\tResults differ!%n");
    }
    return row;
  }

  private void printSummary(List<Row> rows) {
    output.printf("%7s  %10s  %10s%n", "Problem", "Yours", "Claude's");
    for (var row : rows) {
      var line = String.format("%7d  %10s  %10s  %s", row.id(), cell(row.yours()),
          row.claude() == null ? "" : cell(row.claude()), row.note());
      output.printf("%s%n", line.stripTrailing());
    }
  }

  private static String cell(Outcome outcome) {
    return switch (outcome) {
      case Outcome.Solved solved -> ProblemRunner.format(solved.medianNanos());
      case Outcome.TimedOut timedOut -> "timed out";
      case Outcome.Failed failed -> "failed";
    };
  }

  /** The outcomes for one problem; {@code claude} is null when Claude has no solution. */
  private record Row(int id, Outcome yours, Outcome claude) {

    boolean differ() {
      return yours instanceof Outcome.Solved y && claude instanceof Outcome.Solved c
          && !y.answer().equals(c.answer());
    }

    boolean succeeded() {
      return yours instanceof Outcome.Solved
          && (claude == null || claude instanceof Outcome.Solved)
          && !differ();
    }

    String note() {
      if (differ()) {
        return "results differ";
      }
      if (yours instanceof Outcome.Solved y && claude instanceof Outcome.Solved c
          && y.medianNanos() > 0 && c.medianNanos() > 0) {
        var ratio = (double) y.medianNanos() / c.medianNanos();
        return ratio >= 1
            ? String.format(Locale.ROOT, "%.1f× faster", ratio)
            : String.format(Locale.ROOT, "%.1f× slower", 1 / ratio);
      }
      return "";
    }
  }
}
